-- =====================================================================
-- 006_seed_mock_data.sql
-- 앱 화면 개발용 mock 데이터
--
-- 실제 ESP32 이벤트만 보고 싶으면 실행하지 않는다.
-- user 없이 device_code + device_secret 기반으로 테스트 데이터를 만든다.
-- =====================================================================

create schema if not exists extensions;
create extension if not exists pgcrypto with schema extensions;

-- 1. 테스트 ESP32 기기
insert into public.devices (
    id,
    device_code,
    display_name,
    firmware_version,
    device_secret_hash
)
values (
    '33333333-3333-3333-3333-333333333333',
    'pad-001',
    'ESP32 배변 감지기',
    '0.1.0',
    extensions.crypt('replace-with-device-secret', extensions.gen_salt('bf'))
)
on conflict (device_code) do update set
    display_name = excluded.display_name,
    firmware_version = excluded.firmware_version,
    device_secret_hash = excluded.device_secret_hash,
    updated_at = now();

-- 2. 기기 상태
insert into public.device_status (
    device_id,
    connection_state,
    loadcell_ok,
    ultrasonic_ok,
    rssi,
    ip_address,
    last_error,
    updated_at
)
values (
    '33333333-3333-3333-3333-333333333333',
    'ONLINE',
    true,
    true,
    -45,
    '192.168.0.100',
    null,
    now()
)
on conflict (device_id) do update set
    connection_state = excluded.connection_state,
    loadcell_ok = excluded.loadcell_ok,
    ultrasonic_ok = excluded.ultrasonic_ok,
    rssi = excluded.rssi,
    ip_address = excluded.ip_address,
    last_error = excluded.last_error,
    updated_at = now();

-- 3. ESP32 원본 이벤트
-- DEVICE_READY는 최종 정책상 저장하지 않으므로 mock에도 넣지 않는다.
insert into public.sensor_events (
    id,
    device_id,
    seq,
    event_type,
    occurred_at,
    received_at
)
values
(
    '44444444-0000-0000-0000-000000000001',
    '33333333-3333-3333-3333-333333333333',
    1,
    'STOOL_DETECTED',
    now() - interval '6 hours',
    now() - interval '6 hours'
),
(
    '44444444-0000-0000-0000-000000000002',
    '33333333-3333-3333-3333-333333333333',
    2,
    'VISIT_DETECTED',
    now() - interval '3 hours',
    now() - interval '3 hours'
),
(
    '44444444-0000-0000-0000-000000000003',
    '33333333-3333-3333-3333-333333333333',
    3,
    'URINE_DETECTED',
    now() - interval '20 minutes',
    now() - interval '20 minutes'
)
on conflict (device_id, seq) do nothing;

-- 4. 앱 표시용 배변 기록
insert into public.potty_records (
    device_id,
    sensor_event_id,
    record_type,
    source,
    occurred_at,
    note
)
values
(
    '33333333-3333-3333-3333-333333333333',
    '44444444-0000-0000-0000-000000000001',
    'STOOL',
    'DEVICE_AUTO',
    now() - interval '6 hours',
    'Mock 대변 기록'
),
(
    '33333333-3333-3333-3333-333333333333',
    '44444444-0000-0000-0000-000000000002',
    'VISIT',
    'DEVICE_AUTO',
    now() - interval '3 hours',
    'Mock 패드 방문 기록'
),
(
    '33333333-3333-3333-3333-333333333333',
    '44444444-0000-0000-0000-000000000003',
    'URINE',
    'DEVICE_AUTO',
    now() - interval '20 minutes',
    'Mock 소변 기록'
)
on conflict do nothing;
