# Supabase SQL Scripts

이 폴더는 ESP32 배변 감지기와 Supabase DB를 연결하기 위한 SQL 파일을 보관한다.

현재 구조는 Supabase Auth 사용자 기준이 아니라 `device_code + device_secret` 기준이다.

## 핵심 구조

- ESP32는 `device_code`와 원문 `device_secret`으로 RPC를 호출한다.
- DB는 `devices.device_secret_hash`만 저장한다.
- 앱도 사용자가 입력한 `device_code + device_secret`으로 조회 RPC를 호출한다.
- `profiles`, `user_id`, `claimed_at` 기반 소유자 연결은 사용하지 않는다.

## 주요 객체

- `public.devices`
- `public.device_status`
- `public.sensor_events`
- `public.potty_records`
- `public.ingest_sensor_event(...)`
- `public.get_sensor_events_by_device(...)`

## 실행 순서

새 DB를 만들 때:

1. `sql/001_create_schema.sql`
2. `sql/002_ingest_sensor_event_rpc.sql`
3. `sql/003_seed_test_device.sql`
4. `sql/004_rpc_test.sql`

앱 화면 개발용 mock 데이터가 필요할 때만:

- `sql/006_seed_mock_data.sql`

기존 user 기반 DB를 device 기반으로 바꿀 때:

1. `sql/005_device_based_access.sql`
2. `sql/002_ingest_sensor_event_rpc.sql`
3. `sql/003_seed_test_device.sql`
4. `notify pgrst, 'reload schema';`

## 앱 조회 방식

앱은 테이블을 직접 조회하지 않고 아래 RPC를 호출한다.

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

## 주의

- ESP32와 앱에는 Supabase service_role key를 절대 넣지 않는다.
- ESP32와 앱에는 anon key 또는 publishable key만 넣는다.
- `device_secret` 원문은 ESP32와 사용자 입력값으로만 사용한다.
- DB에는 `device_secret_hash`만 저장한다.
