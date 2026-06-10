-- =====================================================================
-- 002_ingest_sensor_event_rpc.sql
-- ESP32 이벤트 수신 RPC + potty_records 자동 생성 trigger
--
-- 전제:
-- 1. 사용자가 제공한 01 스키마 생성 SQL이 먼저 실행되어 있어야 한다.
-- 2. public.devices, public.device_status, public.sensor_events,
--    public.potty_records 테이블이 이미 존재해야 한다.
-- 3. public.sensor_event_type, public.potty_record_type,
--    public.connection_state enum이 이미 존재해야 한다.
--
-- 현재 보안 전제:
-- - RLS는 사용하지 않는다.
-- - 대신 anon/authenticated는 테이블 직접 쓰기를 막는다.
-- - ESP32는 ingest_sensor_event() RPC만 호출한다.
-- =====================================================================

create schema if not exists extensions;
create extension if not exists pgcrypto with schema extensions;

-- ---------------------------------------------------------------------
-- 1. sensor_events → potty_records 자동 생성 trigger function
-- ---------------------------------------------------------------------
-- DEVICE_READY, SENSOR_ERROR는 앱 표시용 배변 기록으로 만들지 않는다.
-- VISIT_DETECTED, URINE_DETECTED, STOOL_DETECTED만 potty_records로 변환한다.

create or replace function public.create_potty_record_from_sensor_event()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
declare
    v_record_type public.potty_record_type;
begin
    case new.event_type::text
        when 'VISIT_DETECTED' then
            v_record_type := 'VISIT';
        when 'URINE_DETECTED' then
            v_record_type := 'URINE';
        when 'STOOL_DETECTED' then
            v_record_type := 'STOOL';
        else
            return new;
    end case;

    insert into public.potty_records (
        device_id,
        sensor_event_id,
        record_type,
        source,
        occurred_at
    )
    values (
        new.device_id,
        new.id,
        v_record_type,
        'DEVICE_AUTO',
        coalesce(new.occurred_at, new.received_at)
    );

    return new;
end;
$$;


drop trigger if exists trg_create_potty_record_from_sensor_event
on public.sensor_events;


create trigger trg_create_potty_record_from_sensor_event
after insert on public.sensor_events
for each row
execute function public.create_potty_record_from_sensor_event();


-- ---------------------------------------------------------------------
-- 2. ESP32가 호출할 RPC function
-- ---------------------------------------------------------------------
-- ESP32는 sensor_events 테이블에 직접 INSERT하지 않는다.
-- ESP32는 아래 RPC만 호출한다.
--
-- POST /rest/v1/rpc/ingest_sensor_event
--
-- payload 예시:
-- {
--   "p_device_code": "pad-001",
--   "p_device_secret": "pad-001-device-secret-1234",
--   "p_seq": 1,
--   "p_event_type": "DEVICE_READY",
--   "p_schema_version": 1,
--   "p_rssi": -55,
--   "p_ip_address": "192.168.0.23"
-- }

drop function if exists public.ingest_sensor_event(
    text,
    text,
    integer,
    public.sensor_event_type,
    timestamptz,
    integer,
    integer,
    inet
);


create function public.ingest_sensor_event(
    p_device_code text,
    p_device_secret text,
    p_seq integer,
    p_event_type public.sensor_event_type,
    p_occurred_at timestamptz default null,
    p_schema_version integer default 1,
    p_rssi integer default null,
    p_ip_address inet default null
)
returns jsonb
language plpgsql
security definer
set search_path = public
as $$
declare
    v_device public.devices%rowtype;
    v_event_id uuid;
    v_duplicate boolean := false;
    v_connection_state public.connection_state;
    v_ultrasonic_ok boolean;
    v_last_error text;
begin
    if p_device_code is null or length(trim(p_device_code)) = 0 then
        raise exception 'INVALID_DEVICE_CODE';
    end if;

    if p_device_secret is null or length(p_device_secret) < 8 then
        raise exception 'INVALID_DEVICE_SECRET';
    end if;

    if p_seq is null or p_seq < 1 then
        raise exception 'INVALID_SEQ';
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

    -- DEVICE_READY는 상태 갱신 신호로만 사용하고 sensor_events에는 저장하지 않는다.
    if p_event_type <> 'DEVICE_READY' then
        insert into public.sensor_events (
            device_id,
            seq,
            schema_version,
            event_type,
            occurred_at
        )
        values (
            v_device.id,
            p_seq,
            coalesce(p_schema_version, 1),
            p_event_type,
            p_occurred_at
        )
        on conflict (device_id, seq)
        do nothing
        returning id into v_event_id;

        if v_event_id is null then
            v_duplicate := true;

            select id
            into v_event_id
            from public.sensor_events
            where device_id = v_device.id
              and seq = p_seq;
        end if;
    end if;

    if p_event_type = 'SENSOR_ERROR' then
        v_connection_state := 'ERROR';
        v_ultrasonic_ok := false;
        v_last_error := 'SENSOR_ERROR';
    else
        v_connection_state := 'ONLINE';
        v_ultrasonic_ok := true;
        v_last_error := null;
    end if;

    update public.devices
    set
        last_seen_at = now(),
        updated_at = now()
    where id = v_device.id;

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
        v_device.id,
        v_connection_state,
        false,
        v_ultrasonic_ok,
        p_rssi,
        p_ip_address,
        v_last_error,
        now()
    )
    on conflict (device_id)
    do update set
        connection_state = excluded.connection_state,
        ultrasonic_ok = excluded.ultrasonic_ok,
        rssi = excluded.rssi,
        ip_address = excluded.ip_address,
        last_error = excluded.last_error,
        updated_at = now();

    return jsonb_build_object(
        'ok', true,
        'event_id', v_event_id,
        'duplicate', v_duplicate,
        'stored', p_event_type <> 'DEVICE_READY'
    );
end;
$$;

-- ---------------------------------------------------------------------
-- 3. 권한 설정
-- ---------------------------------------------------------------------

grant usage on schema public to anon;
grant usage on schema public to authenticated;

revoke insert, update, delete on public.devices from anon, authenticated;
revoke insert, update, delete on public.device_status from anon, authenticated;
revoke insert, update, delete on public.sensor_events from anon, authenticated;
revoke insert, update, delete on public.potty_records from anon, authenticated;

revoke execute on function public.ingest_sensor_event(
    text,
    text,
    integer,
    public.sensor_event_type,
    timestamptz,
    integer,
    integer,
    inet
) from public;

revoke execute on function public.ingest_sensor_event(
    text,
    text,
    integer,
    public.sensor_event_type,
    timestamptz,
    integer,
    integer,
    inet
) from anon;

revoke execute on function public.ingest_sensor_event(
    text,
    text,
    integer,
    public.sensor_event_type,
    timestamptz,
    integer,
    integer,
    inet
) from authenticated;

grant execute on function public.ingest_sensor_event(
    text,
    text,
    integer,
    public.sensor_event_type,
    timestamptz,
    integer,
    integer,
    inet
) to anon;
