-- 001_create_schema.sql
-- Mungjeok Supabase initial schema
-- 패드 포화도 제외 버전 · ESP32 Wi-Fi + Supabase + CMP 앱

create extension if not exists pgcrypto;

-- =========================================================
-- Enum types
-- =========================================================

do $$
begin
create type public.connection_state as enum (
        'SETUP_REQUIRED',
        'ONLINE',
        'OFFLINE',
        'ERROR'
    );
exception
    when duplicate_object then null;
end $$;

do $$
begin
create type public.sensor_event_type as enum (
        'DEVICE_READY',
        'HEARTBEAT',
        'CALIBRATION_DONE',
        'VISIT_START',
        'VISIT_END',
        'URINE_DETECTED',
        'FECES_DETECTED',
        'UNKNOWN',
        'NO_WASTE_DETECTED',
        'SENSOR_ERROR'
    );
exception
    when duplicate_object then null;
end $$;

do $$
begin
create type public.potty_record_type as enum (
        'VISIT',
        'URINE',
        'FECES',
        'MIXED'
    );
exception
    when duplicate_object then null;
end $$;

do $$
begin
create type public.record_source as enum (
        'DEVICE_AUTO'
    );
exception
    when duplicate_object then null;
end $$;

do $$
begin
create type public.confidence_level as enum (
        'HIGH',
        'MEDIUM',
        'LOW',
        'USER_CONFIRMED'
    );
exception
    when duplicate_object then null;
end $$;

-- =========================================================
-- User and pet
-- =========================================================

create table if not exists public.profiles (
                                               id uuid primary key references auth.users(id) on delete cascade,
    nickname text,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
    );

create table if not exists public.pets (
                                           id uuid primary key default gen_random_uuid(),
    owner_id uuid not null references public.profiles(id) on delete cascade,
    name text not null default '내 강아지',
    birth_date date,
    weight_g numeric,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
    );

-- =========================================================
-- Device
-- =========================================================

create table if not exists public.devices (
                                              id uuid primary key default gen_random_uuid(),
    owner_id uuid references public.profiles(id) on delete set null,
    pet_id uuid references public.pets(id) on delete set null,

    device_code text not null unique,
    display_name text not null default 'ESP32 배변 감지기',
    firmware_version text,

    -- 운영에서는 원문 secret이 아니라 hash를 저장한다.
    device_secret_hash text not null,

    claimed_at timestamptz,
    last_seen_at timestamptz,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
    );

create table if not exists public.device_status (
                                                    device_id uuid primary key references public.devices(id) on delete cascade,

    connection_state public.connection_state not null default 'SETUP_REQUIRED',

    loadcell_ok boolean not null default false,
    ultrasonic_ok boolean not null default false,
    calibrated_at timestamptz,

    current_weight_g numeric,
    current_distance_cm numeric,
    rssi integer,
    ip_address inet,

    last_error text,
    updated_at timestamptz not null default now()
    );

create table if not exists public.device_settings (
                                                      device_id uuid primary key references public.devices(id) on delete cascade,

    led_alert_enabled boolean not null default true,

    visit_threshold_g numeric not null default 500,
    urine_delta_g numeric not null default 25,
    feces_delta_g numeric not null default 15,
    feces_height_delta_cm numeric not null default 2.0,

    timezone text not null default 'Asia/Seoul',

    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
    );

-- =========================================================
-- ESP32 raw events
-- =========================================================

create table if not exists public.sensor_events (
                                                    id uuid primary key default gen_random_uuid(),

    device_id uuid not null references public.devices(id) on delete cascade,

    seq integer not null,
    schema_version integer not null default 1,

    event_type public.sensor_event_type not null,
    visit_seq integer,

    occurred_at timestamptz,
    received_at timestamptz not null default now(),
    device_millis bigint,

    weight_g numeric,
    dog_weight_estimate_g numeric,
    pre_visit_pad_weight_g numeric,
    post_visit_pad_weight_g numeric,
    residual_delta_g numeric,
    distance_cm numeric,
    height_delta_cm numeric,

    confidence public.confidence_level,
    raw_payload jsonb not null,

    created_at timestamptz not null default now(),

    constraint sensor_events_device_seq_unique unique (device_id, seq)
    );

create index if not exists sensor_events_device_time_idx
    on public.sensor_events(device_id, coalesce(occurred_at, received_at) desc);

-- =========================================================
-- App display records
-- =========================================================

create table if not exists public.potty_records (
                                                    id uuid primary key default gen_random_uuid(),

    owner_id uuid not null references public.profiles(id) on delete cascade,
    pet_id uuid references public.pets(id) on delete set null,
    device_id uuid references public.devices(id) on delete set null,
    sensor_event_id uuid references public.sensor_events(id) on delete set null,

    -- ESP32 방문 번호. 같은 방문의 VISIT_START / VISIT_END / 배변 이벤트를 묶는 용도.
    visit_seq integer,

    record_type public.potty_record_type not null,
    source public.record_source not null default 'DEVICE_AUTO',

    occurred_at timestamptz not null,
    duration_ms bigint,

    weight_g numeric,
    dog_weight_estimate_g numeric,
    residual_delta_g numeric,
    distance_cm numeric,
    height_delta_cm numeric,

    confidence public.confidence_level,
    note text,

    is_edited boolean not null default false,
    deleted_at timestamptz,

    created_by uuid references public.profiles(id) on delete set null,
    updated_by uuid references public.profiles(id) on delete set null,

    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
    );

create index if not exists potty_records_owner_time_idx
    on public.potty_records(owner_id, occurred_at desc)
    where deleted_at is null;

create index if not exists potty_records_device_time_idx
    on public.potty_records(device_id, occurred_at desc)
    where deleted_at is null;

create index if not exists potty_records_device_visit_idx
    on public.potty_records(device_id, visit_seq)
    where deleted_at is null;
