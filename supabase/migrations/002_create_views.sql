-- 002_create_views.sql
-- App read models for CMP frontend

-- =========================================================
-- 1. Daily summary
-- 홈 화면: 오늘 소변/대변/방문 횟수
-- =========================================================

create or replace view public.v_daily_summaries
with (security_invoker = true)
as
select
    pr.owner_id,
    pr.pet_id,
    pr.device_id,
    (pr.occurred_at at time zone 'Asia/Seoul')::date as record_date,

    count(*) filter (where pr.record_type = 'VISIT') as visit_count,

    count(*) filter (
        where pr.record_type in ('URINE', 'MIXED')
    ) as urine_count,

    count(*) filter (
        where pr.record_type in ('FECES', 'MIXED')
    ) as feces_count,

    min(pr.occurred_at) as first_record_at,
    max(pr.occurred_at) as last_record_at

from public.potty_records pr
where pr.deleted_at is null
group by
    pr.owner_id,
    pr.pet_id,
    pr.device_id,
    (pr.occurred_at at time zone 'Asia/Seoul')::date;

-- =========================================================
-- 2. Latest potty record
-- 홈 화면: 마지막 감지
-- =========================================================

create or replace view public.v_latest_potty_record
with (security_invoker = true)
as
select distinct on (pr.device_id)
    pr.owner_id,
    pr.pet_id,
    pr.device_id,
    pr.id as record_id,
    pr.record_type,
    pr.occurred_at,
    pr.confidence,
    pr.note,
    pr.residual_delta_g,
    pr.height_delta_cm
from public.potty_records pr
where pr.deleted_at is null
order by pr.device_id, pr.occurred_at desc;

-- =========================================================
-- 3. Device connection status
-- 기기 연결 화면: ESP32 연결/센서/보정 상태
-- =========================================================

create or replace view public.v_device_connection_status
with (security_invoker = true)
as
select
    d.id,
    d.owner_id,
    d.pet_id,
    d.device_code,
    d.display_name,
    d.firmware_version,
    d.claimed_at,
    d.last_seen_at,
    ds.connection_state,
    ds.loadcell_ok,
    ds.ultrasonic_ok,
    ds.calibrated_at,
    ds.current_weight_g,
    ds.current_distance_cm,
    ds.rssi,
    ds.last_error,
    ds.updated_at
from public.devices d
         left join public.device_status ds
                   on ds.device_id = d.id;

-- =========================================================
-- 4. Potty record timeline
-- 배변 기록 화면: 날짜별 타임라인
-- =========================================================

create or replace view public.v_potty_record_timeline
with (security_invoker = true)
as
select
    pr.id,
    pr.owner_id,
    pr.pet_id,
    pr.device_id,
    pr.record_type,

    case pr.record_type
        when 'URINE' then '소변'
        when 'FECES' then '대변'
        when 'VISIT' then '패드 방문'
        when 'MIXED' then '소변+대변'
        end as display_label,

    case pr.record_type
        when 'URINE' then 'orange'
        when 'FECES' then 'purple'
        when 'VISIT' then 'green'
        when 'MIXED' then 'blue'
        end as display_color,

    pr.source,
    pr.occurred_at,
    to_char(pr.occurred_at at time zone 'Asia/Seoul', 'HH24:MI') as display_time,
    (pr.occurred_at at time zone 'Asia/Seoul')::date as record_date,

    pr.duration_ms,
    pr.residual_delta_g,
    pr.height_delta_cm,
    pr.confidence,
    pr.note
from public.potty_records pr
where pr.deleted_at is null;
