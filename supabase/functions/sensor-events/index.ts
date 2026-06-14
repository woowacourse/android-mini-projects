// =============================================================================
// 도그널 · device-events Edge Function
// -----------------------------------------------------------------------------
// 역할:
//   ESP32 기기가 Wi-Fi(HTTPS POST)로 보낸 센서 이벤트를 받아서 처리하는 중앙 엔드포인트입니다.
// 
// 주요 흐름:
//   1) 기기 인증 검증 (보안 헤더 확인 및 해시 대조)
//   2) 기기의 최근 접속 상태 갱신 (devices.last_seen_at 및 device_status 갱신)
//   3) 센서가 보낸 "원본 이벤트"를 안전하게 보관 (sensor_events 저장, 중복 방지)
//   4) 이벤트 타입에 따라 보호자 앱에 표시될 "가공된 배변 기록" 생성 (potty_records)
//   5) 처리 결과를 JSON 형태로 ESP32에 응답
// =============================================================================

import { createClient, type SupabaseClient } from "jsr:@supabase/supabase-js@2";

// -----------------------------------------------------------------------------
// 1. 상수 정의
// -----------------------------------------------------------------------------

// DB의 `sensor_event_type` Enum에 정의된 값들
const VALID_EVENT_TYPES = new Set([
  "DEVICE_READY",    // 기기 부팅 완료 및 센서 준비됨
  "VISIT_DETECTED",  // 반려견이 패드에 올라감을 감지
  "URINE_DETECTED",  // 소변 감지됨
  "STOOL_DETECTED",  // 대변 감지됨
  "SENSOR_ERROR"     // 센서 이상 발생
]);

// sensor_events의 event_type이 키로 들어오면, potty_records의 record_type이 값으로 매핑
const EVENT_TO_NEW_RECORD: Record<string, string> = {
  VISIT_DETECTED: "VISIT",
  URINE_DETECTED: "URINE",
  STOOL_DETECTED: "STOOL",
};

// -----------------------------------------------------------------------------
// 2. 작은 유틸리티 함수들
// -----------------------------------------------------------------------------

/** * HTTP JSON 응답을 일관된 형태로 만들어주는 헬퍼 함수
 */
function jsonResponse(status: number, body: unknown): Response {
  return new Response(JSON.stringify(body), {
    status,
    headers: { "Content-Type": "application/json" },
  });
}

/**
 * 평문 문자열을 SHA-256 알고리즘을 사용해 해시화(16진수 문자열)
 * DB(`devices.device_secret_hash`)에는 해킹 피해를 줄이기 위해 원문 비밀키가 아닌
 * 이 해시값이 저장되어 있으므로, 기기가 보낸 키를 여기서 해시화한 뒤 DB값과 비교합니다.
 */
async function sha256Hex(input: string): Promise<string> {
  const data = new TextEncoder().encode(input);
  const digest = await crypto.subtle.digest("SHA-256", data);
  return Array.from(new Uint8Array(digest))
    .map((b) => b.toString(16).padStart(2, "0"))
    .join("");
}

/**
 * 타이밍 공격(Timing Attack)을 방지하기 위한 안전한 문자열 비교 함수
 * 일치 여부와 무관하게 항상 끝까지 비교
 */
function timingSafeEqual(a: string, b: string): boolean {
  if (a.length !== b.length) return false;
  let result = 0;
  for (let i = 0; i < a.length; i++) {
    result |= a.charCodeAt(i) ^ b.charCodeAt(i); // 다르면 result 값이 0이 아니게 됨
  }
  return result === 0;
}

/** * 숫자로 변환할 수 있는 값만 안전하게 number로 바꾸고, 
 * 쓰레기 값이나 undefined일 경우 null로 처리
 */
function toNum(v: unknown): number | null {
  if (v === null || v === undefined) return null;
  const n = Number(v);
  return Number.isFinite(n) ? n : null;
}

// -----------------------------------------------------------------------------
// 3. 메인 핸들러
// -----------------------------------------------------------------------------
Deno.serve(async (req: Request): Promise<Response> => {
  // 함수가 호출된 시각을 기록해둡니다. 수신 시각 저장 및 DB 갱신에 공통으로 쓰임
  const serverReceivedAt = new Date().toISOString();

  // ---- 3-1. HTTP 메서드 확인 -------------------------------------------------
  // ESP32는 데이터를 전송(POST)만 합니다. GET, PUT 등의 잘못된 요청은 차단
  if (req.method !== "POST") {
    return jsonResponse(405, { ok: false, error: "Method Not Allowed", code: "METHOD_NOT_ALLOWED" });
  }

  // ---- 3-2. 인증 헤더 추출 ---------------------------------------------------
  // 기기가 HTTP 헤더에 담아 보낸 인증 정보를 꺼냅니다.
  const deviceCode = req.headers.get("x-device-code");         // 예: "dognal-abc-123"
  const deviceKey = req.headers.get("x-device-key");           // 기기 고유 비밀키
  const firmwareVersion = req.headers.get("x-firmware-version") ?? null; // (선택) 펌웨어 버전

  if (!deviceCode || !deviceKey) {
    return jsonResponse(401, {
      ok: false,
      error: "Missing device credentials",
      code: "MISSING_CREDENTIALS",
    });
  }

  // ---- 3-3. 요청 본문(JSON) 파싱 --------------------------------------------
  let body: Record<string, any>;
  try {
    body = await req.json();
  } catch {
    // JSON 형식이 깨져서 오면 파싱 에러 처리
    return jsonResponse(400, { ok: false, error: "Invalid JSON body", code: "BAD_JSON" });
  }

  const seq = body.seq;             // 이벤트 순서 (네트워크 재전송 시 중복 방지용)
  const eventType = body.eventType; // 발생한 이벤트 종류

  // 필수 파라미터 타입 검증
  if (typeof seq !== "number" || !Number.isInteger(seq)) {
    return jsonResponse(400, { ok: false, error: "'seq' must be an integer", code: "BAD_SEQ" });
  }
  if (typeof eventType !== "string" || !VALID_EVENT_TYPES.has(eventType)) {
    return jsonResponse(400, { ok: false, error: "Unknown 'eventType'", code: "BAD_EVENT_TYPE" });
  }

  // ---- 3-4. Supabase 클라이언트(관리자 권한) 생성 ---------------------------
  // RLS(Row Level Security) 정책을 무시하고 데이터를 강제로 읽고 쓸 수 있는 관리자 키를 사용합니다.
  const supabaseUrl = Deno.env.get("SUPABASE_URL");
  const serviceRoleKey = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY");
  if (!supabaseUrl || !serviceRoleKey) {
    return jsonResponse(500, { ok: false, error: "Server is misconfigured", code: "NO_ENV" });
  }

  // Edge Function은 서버리스로 동작하므로 사용자 세션을 유지할 필요가 없어 false 처리합니다.
  const supabase: SupabaseClient = createClient(supabaseUrl, serviceRoleKey, {
    auth: { persistSession: false, autoRefreshToken: false },
  });

  // ---- 3-5. DB에서 기기 정보 조회 ---------------------------------------------
  // 기기 코드로 DB를 검색해 기기가 실제로 등록되어 있는지, 누구의 소유인지 확인합니다.
  const { data: device, error: deviceErr } = await supabase
    .from("devices")
    .select("id, user_id, device_secret_hash") // user_id가 없으면 아직 사용자가 연결(Claim)하지 않은 기기임
    .eq("device_code", deviceCode)
    .maybeSingle();

  if (deviceErr) {
    return jsonResponse(500, { ok: false, error: "DB error while loading device", code: "DB_DEVICE" });
  }
  if (!device) {
    // 기기를 찾을 수 없더라도 보안상 "Unauthorized"로 통일하여 정보 유출을 막습니다.
    return jsonResponse(401, { ok: false, error: "Unauthorized", code: "UNAUTHORIZED" });
  }

  // ---- 3-6. 기기 비밀키 해시 대조 (인증) --------------------------------------
  // ESP32가 보낸 평문 키를 SHA-256으로 해시화한 뒤, DB에 저장된 해시값과 타이밍 안전 기법으로 비교합니다.
  const providedHash = await sha256Hex(deviceKey);
  if (!timingSafeEqual(providedHash, device.device_secret_hash ?? "")) {
    return jsonResponse(401, { ok: false, error: "Unauthorized", code: "UNAUTHORIZED" });
  }

  const deviceId: string = device.id;
  const userId: string | null = device.user_id;

  // 기기가 자체 RTC(시계)가 없어 occurredAt을 못 보냈다면, 서버가 받은 시간을 발생 시간으로 간주합니다.
  const rawOccurredAt: string | null = typeof body.occurredAt === "string" ? body.occurredAt : null;
  const occurredAt: string = rawOccurredAt ?? serverReceivedAt;

  // ---- 3-7. 기기 최신 연결 상태 갱신 ------------------------------------------
  // 이벤트가 들어왔다는 것은 기기가 온라인 상태라는 뜻이므로 'last_seen_at'을 즉시 갱신합니다.
  await supabase
    .from("devices")
    .update({
      last_seen_at: serverReceivedAt,
      // 펌웨어 버전이 HTTP 헤더로 넘어왔다면 함께 갱신해줍니다.
      ...(firmwareVersion ? { firmware_version: firmwareVersion } : {}),
      updated_at: serverReceivedAt,
    })
    .eq("id", deviceId);

  // device_status 테이블에도 현재 배터리나 연결 상태, 와이파이 강도(rssi) 등을 갱신합니다.
  await updateDeviceStatus(supabase, {
    deviceId,
    eventType,
    serverReceivedAt,
    rssi: toNum(body.rssi),
    lastError: typeof body.message === "string" ? body.message : null,
  });

  // ---- 3-8. 원본 센서 이벤트(sensor_events) 보관 ------------------------------
  // 나중에 분석하거나 디버깅하기 위해 가공되지 않은 데이터를 무조건 남깁니다.
  const insertEvent = {
    device_id: deviceId,
    seq,
    schema_version: typeof body.schemaVersion === "number" ? body.schemaVersion : 1,
    event_type: eventType,
    occurred_at: rawOccurredAt,
    received_at: serverReceivedAt,
    weight_g: toNum(body.weightG),
    distance_cm: toNum(body.distanceCm),
    baseline_weight_g: toNum(body.baselineWeightG),
    baseline_distance_cm: toNum(body.baselineDistanceCm),
  };

  const { data: insertedEvent, error: eventErr } = await supabase
    .from("sensor_events")
    .insert(insertEvent)
    .select("id")
    .single();

  if (eventErr) {
    // 23505는 PostgreSQL의 "Unique constraint violation" 에러 코드입니다.
    // Wi-Fi가 불안정해서 기기가 동일한 seq 번호를 가진 이벤트를 재전송한 경우,
    // 에러로 뱉지 않고 "이미 성공적으로 받았다(duplicate: true)"라고 기기에게 안심시켜줍니다.
    if (eventErr.code === "23505") { 
      const { data: existing } = await supabase
        .from("sensor_events")
        .select("id")
        .eq("device_id", deviceId)
        .eq("seq", seq)
        .maybeSingle();

      return jsonResponse(200, {
        ok: true,
        duplicate: true,
        eventId: existing?.id ?? null,
        serverReceivedAt,
      });
    }
    return jsonResponse(500, { ok: false, error: "DB error while saving event", code: "DB_EVENT" });
  }

  const eventId: string = insertedEvent.id;

  // ---- 3-9. 앱 표시용 배변 기록(potty_records) 생성 ----------------------------
  let recordId: string | null = null;
  let skippedReason: string | null = null;

  // 발생한 이벤트가 사용자가 알아야 하는 배변 이벤트(VISIT, URINE, STOOL)인지 확인합니다.
  if (EVENT_TO_NEW_RECORD[eventType]) {
    // 만약 사용자가 기기 등록(Claim)을 안 해서 user_id가 없다면 기록을 띄워줄 사람이 없습니다.
    // 원본 센서 기록만 남기고, 화면용 기록 생성은 스킵합니다.
    if (!userId) {
      skippedReason = "DEVICE_NOT_CLAIMED"; 
    } else {
      const recordType = EVENT_TO_NEW_RECORD[eventType];
      const { data: rec, error: recErr } = await supabase
        .from("potty_records")
        .insert({
          user_id: userId,          // 어느 사용자의 기록인지 연결
          device_id: deviceId,      // 어느 기기에서 발생했는지 연결
          sensor_event_id: eventId, // 이 배변 기록이 어떤 원본 이벤트로부터 만들어졌는지 추적(FK)
          record_type: recordType,
          source: "DEVICE_AUTO",    // 사용자가 앱에서 수기로 적은게 아니라 기기가 자동으로 감지했음을 명시
          occurred_at: occurredAt,
        })
        .select("id")
        .single();

      if (recErr) {
        // 원본 이벤트 저장은 성공했지만 파생 데이터 생성이 실패한 경우,
        // 기기가 502 에러를 받고 나중에 다시 보내보도록 유도합니다.
        return jsonResponse(502, {
          ok: false,
          error: "Event saved but failed to create record",
          code: "DB_RECORD",
          eventId,
        });
      }
      recordId = rec.id;
    }
  }

  // ---- 3-10. 성공 응답 반환 --------------------------------------------------
  // 기기에게 "잘 받아서 처리했어!" 라고 알려줍니다. 
  // 기기는 200 OK를 받으면 기기 내부에 임시 저장해둔 이벤트를 삭제하고 다음 이벤트를 준비합니다.
  return jsonResponse(200, {
    ok: true,
    duplicate: false,
    eventId,
    serverReceivedAt,
    recordId, // 디버깅 용도로 새로 만들어진 recordId를 응답에 포함
    ...(skippedReason ? { skipped: skippedReason } : {}),
  });
});

// -----------------------------------------------------------------------------
// 4. device_status 갱신 로직 분리
// -----------------------------------------------------------------------------
/**
 * 기기의 현재 상태(device_status)를 상황에 맞게 Upsert(있으면 수정, 없으면 생성) 합니다.
 */
async function updateDeviceStatus(
  supabase: SupabaseClient,
  params: {
    deviceId: string;
    eventType: string;
    serverReceivedAt: string;
    rssi: number | null;
    lastError: string | null;
  },
): Promise<void> {
  const { deviceId, eventType, serverReceivedAt, rssi, lastError } = params;

  // 모든 이벤트 공통 갱신 내용 (연결 중임을 표시)
  const patch: Record<string, unknown> = {
    device_id: deviceId,
    connection_state: "ONLINE",
    updated_at: serverReceivedAt,
  };

  // 와이파이 신호 강도 값이 넘어왔다면 추가
  if (rssi !== null) patch.rssi = rssi;

  // 특정 이벤트 종류에 따른 특별 상태 처리
  switch (eventType) {
    case "DEVICE_READY":
      // 부팅이 정상 완료되었으므로 센서 상태를 전부 OK로 변경
      patch.loadcell_ok = true;
      patch.ultrasonic_ok = true;
      break;
    case "SENSOR_ERROR":
      // 에러 이벤트가 오면 연결 상태를 ERROR로 바꾸고 상세 메시지 기록
      patch.connection_state = "ERROR";
      patch.last_error = lastError ?? "SENSOR_ERROR";
      break;
  }

  // device_id를 기준으로 충돌이 발생하면(onConflict), 덮어쓰기를 수행합니다.
  await supabase.from("device_status").upsert(patch, { onConflict: "device_id" });
}
