-- =====================================================================
-- 005_device_based_access.sql
-- 사용자 계정 없이 device_code + device_secret으로 기기 이벤트 조회
--
-- 목적:
-- - 앱 사용자가 device_code와 device_secret을 입력한다.
-- - Supabase RPC가 secret hash를 검증한다.
-- - 검증 성공 시 해당 기기의 sensor_events를 반환한다.
-- - user_id/profiles 기반 연결은 사용하지 않는다.
-- =====================================================================

create schema if not exists extensions;
create extension if not exists pgcrypto with schema extensions;

-- ---------------------------------------------------------------------
-- 1. 기존 DB를 device_secret_hash 기준으로 보정
-- ---------------------------------------------------------------------

alter table public.devices
add column if not exists device_secret_hash text;

do $$
begin
    if exists (
        select 1
        from information_schema.columns
        where table_schema = 'public'
          and table_name = 'devices'
          and column_name = 'device_secret'
    ) then
        execute $sql$
            update public.devices
            set device_secret_hash = extensions.crypt(device_secret, extensions.gen_salt('bf'))
            where device_secret_hash is null
              and device_secret is not null
        $sql$;
    end if;
end;
$$;

update public.devices
set
    device_secret_hash = extensions.crypt(
        'replace-with-device-secret',
        extensions.gen_salt('bf')
    ),
    updated_at = now()
where device_code = 'pad-001';

update public.devices
set
    device_secret_hash = extensions.crypt(
        gen_random_uuid()::text,
        extensions.gen_salt('bf')
    ),
    updated_at = now()
where device_secret_hash is null;

alter table public.devices
alter column device_secret_hash set not null;

-- ---------------------------------------------------------------------
-- 2. user 기반 컬럼/객체 제거
-- ---------------------------------------------------------------------

drop trigger if exists on_auth_user_created on auth.users;
drop function if exists public.handle_new_user();

drop index if exists public.devices_user_idx;
drop index if exists public.potty_records_user_time_idx;
drop index if exists public.potty_records_user_type_time_idx;

alter table public.potty_records
drop column if exists user_id;

alter table public.devices
drop column if exists user_id;

alter table public.devices
drop column if exists claimed_at;

alter table public.devices
drop column if exists device_secret;

drop table if exists public.profiles cascade;

delete from public.potty_records
where device_id is null;

alter table public.potty_records
drop constraint if exists potty_records_device_id_fkey;

alter table public.potty_records
alter column device_id set not null;

alter table public.potty_records
add constraint potty_records_device_id_fkey
foreign key (device_id)
references public.devices(id)
on delete cascade;

create index if not exists potty_records_device_type_time_idx
on public.potty_records(device_id, record_type, occurred_at desc);

-- ---------------------------------------------------------------------
-- 3. device_code + device_secret 검증 후 sensor_events 조회 RPC
-- ---------------------------------------------------------------------

drop function if exists public.get_sensor_events_by_device(text, text, integer);

create function public.get_sensor_events_by_device(
    p_device_code text,
    p_device_secret text,
    p_limit integer default 50
)
returns table (
    event_id uuid,
    device_code text,
    seq integer,
    schema_version integer,
    event_type public.sensor_event_type,
    occurred_at timestamptz,
    received_at timestamptz,
    created_at timestamptz,
    occurred_at_kst timestamp,
    received_at_kst timestamp,
    created_at_kst timestamp
)
language plpgsql
security definer
set search_path = public
as $$
declare
    v_device public.devices%rowtype;
    v_limit integer;
begin
    if p_device_code is null or length(trim(p_device_code)) = 0 then
        raise exception 'INVALID_DEVICE_CODE';
    end if;

    if p_device_secret is null or length(p_device_secret) < 8 then
        raise exception 'INVALID_DEVICE_SECRET';
    end if;

    select *
    into v_device
    from public.devices
    where device_code = p_device_code;

    if not found then
        raise exception 'INVALID_DEVICE';
    end if;

    if extensions.crypt(p_device_secret, v_device.device_secret_hash) <> v_device.device_secret_hash then
        raise exception 'INVALID_DEVICE_SECRET';
    end if;

    v_limit := least(greatest(coalesce(p_limit, 50), 1), 200);

    return query
    select
        se.id as event_id,
        v_device.device_code,
        se.seq,
        se.schema_version,
        se.event_type,
        se.occurred_at,
        se.received_at,
        se.created_at,
        se.occurred_at at time zone 'Asia/Seoul' as occurred_at_kst,
        se.received_at at time zone 'Asia/Seoul' as received_at_kst,
        se.created_at at time zone 'Asia/Seoul' as created_at_kst
    from public.sensor_events se
    where se.device_id = v_device.id
    order by coalesce(se.occurred_at, se.received_at) desc
    limit v_limit;
end;
$$;

revoke execute on function public.get_sensor_events_by_device(text, text, integer) from public;
grant execute on function public.get_sensor_events_by_device(text, text, integer) to anon;

notify pgrst, 'reload schema';
