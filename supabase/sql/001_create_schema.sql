-- =====================================================================
-- 001_create_schema.sql
-- DOGNAL device_code + device_secret 기반 최종 스키마
-- =====================================================================

create schema if not exists extensions;
create extension if not exists pgcrypto with schema extensions;

-- ---------------------------------------------------------------------
-- ENUM 타입
-- ---------------------------------------------------------------------

create type public.connection_state as enum (
    'SETUP_REQUIRED',
    'ONLINE',
    'OFFLINE',
    'ERROR'
);

create type public.sensor_event_type as enum (
    'DEVICE_READY',
    'VISIT_DETECTED',
    'URINE_DETECTED',
    'STOOL_DETECTED',
    'SENSOR_ERROR'
);

create type public.potty_record_type as enum (
    'VISIT',
    'URINE',
    'STOOL'
);

create type public.record_source as enum (
    'DEVICE_AUTO'
);

-- ---------------------------------------------------------------------
-- 기기 정보
-- ---------------------------------------------------------------------

create table public.devices (
    id uuid primary key default gen_random_uuid(),

    device_code text not null unique,
    display_name text not null default 'ESP32 배변 감지기',
    firmware_version text,

    -- 원문 secret이 아니라 hash를 저장한다.
    device_secret_hash text not null,

    last_seen_at timestamptz,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

-- ---------------------------------------------------------------------
-- 기기 상태
-- ---------------------------------------------------------------------

create table public.device_status (
    device_id uuid primary key references public.devices(id) on delete cascade,

    connection_state public.connection_state not null default 'SETUP_REQUIRED',

    loadcell_ok boolean not null default false,
    ultrasonic_ok boolean not null default false,

    rssi integer,
    ip_address inet,

    last_error text,
    updated_at timestamptz not null default now()
);

-- ---------------------------------------------------------------------
-- ESP32 원본 이벤트
-- ---------------------------------------------------------------------

create table public.sensor_events (
    id uuid primary key default gen_random_uuid(),

    device_id uuid not null references public.devices(id) on delete cascade,

    seq integer not null,
    schema_version integer not null default 1,

    event_type public.sensor_event_type not null,

    weight_g real,
    distance_cm real,
    baseline_weight_g real,
    baseline_distance_cm real,

    occurred_at timestamptz,
    received_at timestamptz not null default now(),
    created_at timestamptz not null default now(),

    constraint sensor_events_device_seq_unique unique (device_id, seq)
);

create index sensor_events_device_time_idx
on public.sensor_events(device_id, coalesce(occurred_at, received_at) desc);

-- ---------------------------------------------------------------------
-- 앱 표시용 배변 기록
-- ---------------------------------------------------------------------

create table public.potty_records (
    id uuid primary key default gen_random_uuid(),

    device_id uuid not null references public.devices(id) on delete cascade,
    sensor_event_id uuid references public.sensor_events(id) on delete set null,

    record_type public.potty_record_type not null,
    source public.record_source not null default 'DEVICE_AUTO',

    occurred_at timestamptz not null,
    note text,

    created_at timestamptz not null default now()
);

create index potty_records_device_time_idx
on public.potty_records(device_id, occurred_at desc);

create index potty_records_device_type_time_idx
on public.potty_records(device_id, record_type, occurred_at desc);
