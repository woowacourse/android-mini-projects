# device_code + device_secret 기반 Supabase 전환 가이드

## 목표

기존 사용자 계정 기반 구조를 제거하고, 앱 사용자가 직접 입력한 `device_code + device_secret`으로 해당 기기의 `sensor_events`를 조회하도록 바꾼다.

변경 후 구조:

```text
ESP32
-> device_code + device_secret으로 이벤트 전송
-> Supabase RPC가 secret hash 검증
-> sensor_events 저장

App
-> 사용자가 device_code + device_secret 입력
-> Supabase RPC가 secret hash 검증
-> 해당 기기의 sensor_events 반환
```

## 핵심 개념

`device_code`는 기기 식별자다.

```text
예: pad-001
```

`device_secret`은 기기 접근 비밀번호다.

```text
예: pad-001-device-secret-1234
```

DB에는 원문 secret을 저장하지 않고 hash만 저장한다.

```text
devices.device_secret_hash
```

## 수정된 파일

```text
supabase/migrations/001_create_schema.sql
supabase/migrations/004_seed_mock_data.sql
supabase/sql/002_ingest_sensor_event_rpc.sql
supabase/sql/003_seed_test_device.sql
supabase/sql/004_rpc_test.sql
supabase/sql/005_device_based_access.sql
supabase/README.md
```

Arduino 코드는 이미 `p_device_code`, `p_device_secret`을 보내고 있으므로 구조 변경이 거의 필요 없다.

확인할 파일:

```text
arduino/stool-sensing/src/config/config.h
arduino/stool-sensing/src/services/supabase_client.cpp
```

## 기존 Supabase DB에 적용하는 순서

Supabase Dashboard의 SQL Editor에서 아래 순서대로 실행한다.

### 1. device 기반 전환 SQL 실행

아래 파일 전체를 실행한다.

```text
supabase/sql/005_device_based_access.sql
```

이 파일이 하는 일:

```text
pgcrypto 준비
device_secret_hash 컬럼 보정
기존 device_secret 원문을 hash로 이전
user_id / claimed_at / profiles 제거
potty_records를 device_id 기준으로 변경
get_sensor_events_by_device RPC 생성
PostgREST schema reload
```

### 2. ESP32 이벤트 수신 RPC 재생성

아래 파일 전체를 다시 실행한다.

```text
supabase/sql/002_ingest_sensor_event_rpc.sql
```

이 파일이 하는 일:

```text
ingest_sensor_event RPC 생성
sensor_events -> potty_records trigger 생성
device_secret_hash 검증
DEVICE_READY는 상태만 갱신하고 sensor_events에는 저장하지 않음
```

### 3. 테스트 기기 등록

아래 파일 전체를 실행한다.

```text
supabase/sql/003_seed_test_device.sql
```

현재 테스트 기기:

```text
device_code: pad-001
device_secret: pad-001-device-secret-1234
```

DB에는 아래처럼 hash로 저장된다.

```sql
extensions.crypt('pad-001-device-secret-1234', extensions.gen_salt('bf'))
```

### 4. Schema reload

마지막으로 실행한다.

```sql
notify pgrst, 'reload schema';
```

## 동작 테스트

### ESP32 이벤트 저장 테스트

`DEVICE_READY`는 기기 온라인 상태 확인용 신호이므로 `sensor_events`에는 저장하지 않고 `devices.last_seen_at`과 `device_status`만 갱신한다.

```sql
select public.ingest_sensor_event(
    'pad-001',
    'pad-001-device-secret-1234',
    900001,
    'DEVICE_READY'
);
```

실제 감지 기록 저장 테스트는 `VISIT_DETECTED`로 확인한다.

```sql
select public.ingest_sensor_event(
    'pad-001',
    'pad-001-device-secret-1234',
    900002,
    'VISIT_DETECTED'
);
```

성공 예시:

```json
{
  "ok": true,
  "event_id": "...",
  "duplicate": false
}
```

### 앱 조회 RPC 테스트

```sql
select *
from public.get_sensor_events_by_device(
    'pad-001',
    'pad-001-device-secret-1234',
    20
);
```

결과가 나오면 앱도 같은 방식으로 조회할 수 있다.

## 앱에서 호출할 RPC

앱은 아래 RPC를 호출한다.

```http
POST /rest/v1/rpc/get_sensor_events_by_device
```

payload:

```json
{
  "p_device_code": "pad-001",
  "p_device_secret": "pad-001-device-secret-1234",
  "p_limit": 50
}
```

반환 필드:

```text
event_id
device_code
seq
schema_version
event_type
occurred_at
received_at
created_at
occurred_at_kst
received_at_kst
created_at_kst
```

PostgreSQL의 `timestamptz`는 실제 저장 기준이 UTC다. Supabase Table Editor에서 오전 8시로 보여도 한국 시간으로는 오후 5시일 수 있다. 앱 표시에는 `*_kst` 필드를 사용하거나 앱에서 `Asia/Seoul`로 변환한다.

## 확인 쿼리

기기 확인:

```sql
select
  id,
  device_code,
  display_name,
  firmware_version,
  last_seen_at,
  created_at,
  updated_at
from public.devices
where device_code = 'pad-001';
```

이벤트 확인:

```sql
select
  d.device_code,
  se.seq,
  se.event_type,
  se.received_at,
  se.received_at at time zone 'Asia/Seoul' as received_at_kst,
  se.created_at
from public.sensor_events se
join public.devices d on d.id = se.device_id
where d.device_code = 'pad-001'
order by se.received_at desc
limit 20;
```

기기 상태 확인:

```sql
select
  d.device_code,
  ds.connection_state,
  ds.ultrasonic_ok,
  ds.rssi,
  ds.ip_address,
  ds.updated_at
from public.device_status ds
join public.devices d on d.id = ds.device_id
where d.device_code = 'pad-001';
```

## 주의

`device_code`는 고유해야 한다.

```sql
device_code text not null unique
```

`device_secret`은 사실상 기기 접근 비밀번호다. 앱에서 사용자가 입력하더라도 DB에는 원문을 저장하지 않는다.

운영 단계에서는 QR 코드나 초기 등록 카드에 아래 두 값을 함께 제공하는 방식을 추천한다.

```text
device_code
device_secret
```
