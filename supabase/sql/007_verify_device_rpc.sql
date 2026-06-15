-- =====================================================================
-- 007_verify_device_rpc.sql
-- 앱이 기기를 연결할 때 호출하는 RPC
--
-- 역할:
-- - 사용자가 앱에서 device_code + device_key를 입력하면
--   bcrypt 검증 후 해당 기기의 uuid를 반환한다.
-- - 검증 실패 시 null을 반환한다.
--
-- 호출처:
-- - Dognal/shared/.../DeviceDataSource.kt → verify_device RPC
-- =====================================================================

create schema if not exists extensions;
create extension if not exists pgcrypto with schema extensions;

drop function if exists public.verify_device(text, text);

create function public.verify_device(
    p_device_code text,
    p_device_key text
)
returns uuid
language plpgsql
security definer
set search_path = public
as $$
declare
    v_id uuid;
begin
    select id
      into v_id
    from public.devices
    where device_code = p_device_code
      and device_secret_hash = extensions.crypt(p_device_key, device_secret_hash);

    return v_id;
end;
$$;

revoke execute on function public.verify_device(text, text) from public;
revoke execute on function public.verify_device(text, text) from authenticated;

grant execute on function public.verify_device(text, text) to anon;
