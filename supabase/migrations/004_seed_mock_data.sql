-- 004_seed_mock_data.sql
-- Mock data for frontend development
--
-- 실행 전 확인:
-- 1. Supabase Dashboard > Authentication > Users 에서 테스트 유저를 만든다.
-- 2. 아래 test_user_id 값을 실제 Auth User UUID로 바꾼다.
-- 3. SQL Editor에서 실행한다.

do $$
declare
test_user_id uuid := '11111111-1111-1111-1111-111111111111'; -- TODO: 실제 auth.users.id로 교체
    test_pet_id uuid := '22222222-2222-2222-2222-222222222222';
    test_device_id uuid := '33333333-3333-3333-3333-333333333333';
begin
insert into public.profiles (id, nickname)
values (test_user_id, '테스트 보호자')
    on conflict (id) do update
                            set nickname = excluded.nickname;

insert into public.pets (id, owner_id, name, weight_g)
values (
           test_pet_id,
           test_user_id,
           '멍멍이',
           4820
       )
    on conflict (id) do update
                            set
                                owner_id = excluded.owner_id,
                            name = excluded.name,
                            weight_g = excluded.weight_g;

insert into public.devices (
    id,
    owner_id,
    pet_id,
    device_code,
    display_name,
    firmware_version,
    device_secret_hash,
    claimed_at,
    last_seen_at
)
values (
           test_device_id,
           test_user_id,
           test_pet_id,
           'pad-001',
           'ESP32 배변 감지기',
           '0.1.0',
           'mock-secret-hash',
           now(),
           now()
       )
    on conflict (device_code) do update
                                     set
                                         owner_id = excluded.owner_id,
                                     pet_id = excluded.pet_id,
                                     display_name = excluded.display_name,
                                     firmware_version = excluded.firmware_version,
                                     last_seen_at = excluded.last_seen_at;

insert into public.device_status (
    device_id,
    connection_state,
    loadcell_ok,
    ultrasonic_ok,
    calibrated_at,
    current_weight_g,
    current_distance_cm,
    rssi,
    updated_at
)
values (
           test_device_id,
           'ONLINE',
           true,
           true,
           now(),
           0,
           18.5,
           -55,
           now()
       )
    on conflict (device_id) do update
                                   set
                                       connection_state = excluded.connection_state,
                                   loadcell_ok = excluded.loadcell_ok,
                                   ultrasonic_ok = excluded.ultrasonic_ok,
                                   calibrated_at = excluded.calibrated_at,
                                   current_weight_g = excluded.current_weight_g,
                                   current_distance_cm = excluded.current_distance_cm,
                                   rssi = excluded.rssi,
                                   updated_at = excluded.updated_at;

insert into public.device_settings (device_id, led_alert_enabled)
values (test_device_id, true)
    on conflict (device_id) do update
                                   set led_alert_enabled = excluded.led_alert_enabled;

-- 같은 seed를 다시 실행할 때 타임라인이 계속 늘어나는 것을 피하고 싶으면 아래 delete를 유지한다.
delete from public.potty_records
where device_id = test_device_id
  and source = 'DEVICE_AUTO';

insert into public.potty_records (
    owner_id,
    pet_id,
    device_id,
    visit_seq,
    record_type,
    source,
    occurred_at,
    duration_ms,
    confidence,
    residual_delta_g,
    height_delta_cm
)
values
    (
        test_user_id,
        test_pet_id,
        test_device_id,
        4,
        'URINE',
        'DEVICE_AUTO',
        now() - interval '20 minutes',
        null,
        'HIGH',
        32.5,
        0.1
    ),
    (
        test_user_id,
        test_pet_id,
        test_device_id,
        3,
        'VISIT',
        'DEVICE_AUTO',
        now() - interval '3 hours',
        125000,
        'HIGH',
        null,
        null
    ),
    (
        test_user_id,
        test_pet_id,
        test_device_id,
        2,
        'FECES',
        'DEVICE_AUTO',
        now() - interval '6 hours',
        null,
        'HIGH',
        58.0,
        3.4
    ),
    (
        test_user_id,
        test_pet_id,
        test_device_id,
        1,
        'URINE',
        'DEVICE_AUTO',
        now() - interval '8 hours',
        null,
        'HIGH',
        28.0,
        0.2
    );
end $$;
