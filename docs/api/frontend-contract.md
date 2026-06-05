# Frontend Data Contract v0.1

이 문서는 CMP 앱에서 Supabase를 조회할 때 사용하는 View 4개와 필드 계약을 정의한다.

## 1. 공통 규칙

### 인증

프론트엔드는 Supabase Auth 로그인 후 `anon key`로 조회한다.

```ts
const supabase = createClient(SUPABASE_URL, SUPABASE_ANON_KEY)
```

`service_role key`는 앱에 포함하지 않는다.

### 조회 대상 View

| 화면 | View |
|---|---|
| 홈 오늘 요약 | `v_daily_summaries` |
| 홈 마지막 감지 | `v_latest_potty_record` |
| 기기 연결 | `v_device_connection_status` |
| 배변 기록 타임라인 | `v_potty_record_timeline` |

---

## 2. Enum

### record_type

| 값 | 앱 표시 |
|---|---|
| `VISIT` | 패드 방문 |
| `URINE` | 소변 |
| `FECES` | 대변 |
| `MIXED` | 소변+대변 |

### connection_state

| 값 | 의미 |
|---|---|
| `SETUP_REQUIRED` | 초기 연결 필요 |
| `ONLINE` | 온라인 |
| `OFFLINE` | 오프라인 |
| `ERROR` | 오류 |

---

## 3. Home 화면

### 3.1 오늘 요약

#### View

```text
v_daily_summaries
```

#### Supabase JS 예시

```ts
const { data, error } = await supabase
  .from('v_daily_summaries')
  .select('*')
  .eq('device_id', deviceId)
  .eq('record_date', today)
  .single()
```

#### Response Type

```ts
type DailySummary = {
  owner_id: string
  pet_id: string | null
  device_id: string
  record_date: string
  visit_count: number
  urine_count: number
  feces_count: number
  first_record_at: string | null
  last_record_at: string | null
}
```

#### UI 매핑

| UI | 필드 |
|---|---|
| 오늘 방문 횟수 | `visit_count` |
| 오늘 소변 횟수 | `urine_count` |
| 오늘 대변 횟수 | `feces_count` |

---

### 3.2 마지막 감지

#### View

```text
v_latest_potty_record
```

#### Supabase JS 예시

```ts
const { data, error } = await supabase
  .from('v_latest_potty_record')
  .select('*')
  .eq('device_id', deviceId)
  .single()
```

#### Response Type

```ts
type LatestPottyRecord = {
  owner_id: string
  pet_id: string | null
  device_id: string
  record_id: string
  record_type: 'VISIT' | 'URINE' | 'FECES' | 'MIXED'
  occurred_at: string
  confidence: 'HIGH' | 'MEDIUM' | 'LOW' | 'USER_CONFIRMED' | null
  note: string | null
  residual_delta_g: number | null
  height_delta_cm: number | null
}
```

---

## 4. 기기 연결 화면

### View

```text
v_device_connection_status
```

### Supabase JS 예시

```ts
const { data, error } = await supabase
  .from('v_device_connection_status')
  .select('*')
  .limit(1)
  .single()
```

### Response Type

```ts
type DeviceConnectionStatus = {
  id: string
  owner_id: string | null
  pet_id: string | null
  device_code: string
  display_name: string
  firmware_version: string | null
  claimed_at: string | null
  last_seen_at: string | null
  connection_state: 'SETUP_REQUIRED' | 'ONLINE' | 'OFFLINE' | 'ERROR' | null
  loadcell_ok: boolean | null
  ultrasonic_ok: boolean | null
  calibrated_at: string | null
  current_weight_g: number | null
  current_distance_cm: number | null
  rssi: number | null
  last_error: string | null
  updated_at: string | null
}
```

### UI 매핑

| UI | 필드 |
|---|---|
| 기기 이름 | `display_name` |
| 연결 상태 | `connection_state` |
| 로드셀 정상 여부 | `loadcell_ok` |
| 초음파 정상 여부 | `ultrasonic_ok` |
| 보정 완료 여부 | `calibrated_at !== null` |
| 오류 메시지 | `last_error` |

---

## 5. 배변 기록 타임라인 화면

### View

```text
v_potty_record_timeline
```

### Supabase JS 예시

```ts
const { data, error } = await supabase
  .from('v_potty_record_timeline')
  .select('*')
  .eq('device_id', deviceId)
  .eq('record_date', selectedDate)
  .order('occurred_at', { ascending: false })
```

### Response Type

```ts
type PottyRecordTimelineItem = {
  id: string
  owner_id: string
  pet_id: string | null
  device_id: string | null
  record_type: 'VISIT' | 'URINE' | 'FECES' | 'MIXED'
  display_label: '패드 방문' | '소변' | '대변' | '소변+대변'
  display_color: 'green' | 'orange' | 'purple' | 'blue'
  source: 'DEVICE_AUTO'
  occurred_at: string
  display_time: string
  record_date: string
  duration_ms: number | null
  residual_delta_g: number | null
  height_delta_cm: number | null
  confidence: 'HIGH' | 'MEDIUM' | 'LOW' | 'USER_CONFIRMED' | null
  note: string | null
}
```

### UI 매핑

| UI | 필드 |
|---|---|
| 시간 | `display_time` |
| 카드 문구 | `display_label` |
| 타임라인 색상 | `display_color` |
| 정렬 기준 | `occurred_at desc` |

---

## 6. 화면별 조회 요약

```text
Home
- v_daily_summaries
- v_latest_potty_record

Device Connect
- v_device_connection_status

Record Timeline
- v_potty_record_timeline
```
