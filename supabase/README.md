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
- `public.device_push_tokens`
- `public.ingest_sensor_event(...)`
- `public.get_sensor_events_by_device(...)`
- `public.verify_device(...)`
- `public.register_device_push_token(...)`
- `notify-potty-record-insert` Edge Function

## 실행 순서

새 DB를 만들 때:

1. `sql/001_create_schema.sql`
2. `sql/002_ingest_sensor_event_rpc.sql`
3. `sql/003_seed_test_device.sql`
4. `sql/004_rpc_test.sql`

앱 화면 개발용 mock 데이터가 필요할 때만:

- `sql/006_seed_mock_data.sql`

푸시 알림을 사용할 때:

1. `sql/008_device_push_tokens.sql`
2. `sql/009_push_notification_permissions.sql`
3. `functions/notify-potty-record-insert` 배포
4. `sql/010_notify_potty_record_insert_webhook.sql`의 `replace-with-webhook-secret`을 실제 `WEBHOOK_SECRET` 값으로 바꿔 실행

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
  "p_device_secret": "replace-with-device-secret",
  "p_limit": 50
}
```

## 주의

- ESP32와 앱에는 Supabase service_role key를 절대 넣지 않는다.
- ESP32와 앱에는 anon key 또는 publishable key만 넣는다.
- `device_secret` 원문은 ESP32와 사용자 입력값으로만 사용한다.
- DB에는 `device_secret_hash`만 저장한다.
- Firebase service account JSON은 Git에 올리지 않고 Supabase Edge Function secret으로만 등록한다.

## 푸시 알림 Edge Function

`potty_records`에 새 row가 INSERT되면 Database Webhook이
`notify-potty-record-insert` Edge Function을 호출한다.

필요한 Supabase secrets:

```bash
FCM_SERVICE_ACCOUNT_JSON='{"type":"service_account", ... }'
FIREBASE_PROJECT_ID='your-firebase-project-id'
WEBHOOK_SECRET='replace-with-long-random-string'
```

`SUPABASE_URL`, `SUPABASE_SERVICE_ROLE_KEY`는 Supabase Edge Functions 기본 secret을 사용한다.

`FCM_SERVICE_ACCOUNT_JSON`은 Firebase Console에서 발급한다.

1. Firebase Console > Project settings
2. Service accounts
3. Firebase Admin SDK
4. Generate new private key
5. 내려받은 JSON 전체를 Supabase secret으로 등록

배포 예시:

```bash
supabase functions deploy notify-potty-record-insert --no-verify-jwt
supabase secrets set FCM_SERVICE_ACCOUNT_JSON='{"type":"service_account", ... }'
supabase secrets set FIREBASE_PROJECT_ID='your-firebase-project-id'
supabase secrets set WEBHOOK_SECRET='replace-with-long-random-string'
```

DB Trigger 설정:

Dashboard Database Webhook 생성이 실패하거나 보이지 않으면 `pg_net` 기반 trigger를 직접 만든다.
`sql/010_notify_potty_record_insert_webhook.sql`의 아래 값은 실제 배포 환경에서만 바꿔 실행한다.

```sql
'x-webhook-secret', 'replace-with-webhook-secret'
```

실행 후 확인:

```sql
select
    trigger_name,
    event_manipulation,
    event_object_table
from information_schema.triggers
where event_object_schema = 'public'
  and event_object_table = 'potty_records';
```

`trg_notify_potty_record_insert`가 나오면 등록된 것이다.

Dashboard Webhook을 사용할 수 있는 경우 설정:

- Table: `public.potty_records`
- Events: `Insert`
- Type: HTTP Request
- Method: `POST`
- URL: `https://<project-ref>.supabase.co/functions/v1/notify-potty-record-insert`
- Header: `x-webhook-secret: <WEBHOOK_SECRET 값>`
