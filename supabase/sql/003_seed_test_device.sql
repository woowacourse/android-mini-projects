-- =====================================================================
-- 003_seed_test_device.sql
-- 초음파 테스트용 ESP32 기기 등록
--
-- 운영에서는 device_code와 device_secret을 기기마다 다르게 발급한다.
-- =====================================================================

create schema if not exists extensions;
create extension if not exists pgcrypto with schema extensions;

insert into public.devices (
    device_code,
    display_name,
    firmware_version,
    device_secret_hash
)
values (
    'pad-001',
    'ESP32 초음파 테스트 기기',
    'stool-sensing-0.1.0',
    extensions.crypt('pad-001-device-secret-1234', extensions.gen_salt('bf'))
)
on conflict (device_code)
do update set
    display_name = excluded.display_name,
    firmware_version = excluded.firmware_version,
    device_secret_hash = excluded.device_secret_hash,
    updated_at = now();


