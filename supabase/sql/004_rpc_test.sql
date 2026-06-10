-- =====================================================================
-- 004_rpc_test.sql
-- SQL Editor에서 RPC 동작 확인
--
-- 주의:
-- ESP32 실제 seq와 충돌하지 않게 테스트 seq는 900001부터 사용한다.
-- =====================================================================


-- DEVICE_READY는 device_status만 갱신하고 sensor_events에는 저장하지 않는다.
select public.ingest_sensor_event(
    'pad-001',
    'pad-001-device-secret-1234',
    900001,
    'DEVICE_READY'
);


-- VISIT_DETECTED는 sensor_events에 들어가고,
-- trigger가 potty_records에 VISIT 기록도 생성한다.
select public.ingest_sensor_event(
    'pad-001',
    'pad-001-device-secret-1234',
    900002,
    'VISIT_DETECTED'
);


-- STOOL_DETECTED는 sensor_events에 들어가고,
-- trigger가 potty_records에 STOOL 기록도 생성한다.
select public.ingest_sensor_event(
    'pad-001',
    'pad-001-device-secret-1234',
    900003,
    'STOOL_DETECTED'
);


-- sensor_events 확인
select
    id,
    device_id,
    seq,
    event_type,
    occurred_at,
    occurred_at at time zone 'Asia/Seoul' as occurred_at_kst,
    received_at,
    received_at at time zone 'Asia/Seoul' as received_at_kst,
    created_at
from public.sensor_events
order by received_at desc
limit 20;


-- 앱에서 사용할 device_code + device_secret 기반 조회 RPC 확인
select *
from public.get_sensor_events_by_device(
    'pad-001',
    'pad-001-device-secret-1234',
    20
);


-- potty_records 확인
select
    id,
    device_id,
    sensor_event_id,
    record_type,
    source,
    occurred_at,
    occurred_at at time zone 'Asia/Seoul' as occurred_at_kst,
    created_at
from public.potty_records
order by created_at desc
limit 20;


-- device_status 확인
select
    d.device_code,
    ds.connection_state,
    ds.loadcell_ok,
    ds.ultrasonic_ok,
    ds.rssi,
    ds.ip_address,
    ds.last_error,
    ds.updated_at,
    ds.updated_at at time zone 'Asia/Seoul' as updated_at_kst
from public.device_status ds
join public.devices d on d.id = ds.device_id
where d.device_code = 'pad-001';
