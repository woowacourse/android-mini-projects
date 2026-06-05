-- 003_create_rls.sql
-- Row Level Security policies
-- 앱 사용자는 자기 데이터만 조회한다.

-- =========================================================
-- Enable RLS
-- =========================================================

alter table public.profiles enable row level security;
alter table public.pets enable row level security;
alter table public.devices enable row level security;
alter table public.device_status enable row level security;
alter table public.device_settings enable row level security;
alter table public.sensor_events enable row level security;
alter table public.potty_records enable row level security;

-- =========================================================
-- Recreate select policies
-- =========================================================

drop policy if exists "profiles_select_own" on public.profiles;
create policy "profiles_select_own"
on public.profiles
for select
                    to authenticated
                    using ((select auth.uid()) = id);

drop policy if exists "pets_select_own" on public.pets;
create policy "pets_select_own"
on public.pets
for select
                    to authenticated
                    using ((select auth.uid()) = owner_id);

drop policy if exists "devices_select_own" on public.devices;
create policy "devices_select_own"
on public.devices
for select
                    to authenticated
                    using ((select auth.uid()) = owner_id);

drop policy if exists "potty_records_select_own" on public.potty_records;
create policy "potty_records_select_own"
on public.potty_records
for select
                    to authenticated
                    using ((select auth.uid()) = owner_id);

drop policy if exists "device_status_select_own_device" on public.device_status;
create policy "device_status_select_own_device"
on public.device_status
for select
                    to authenticated
                    using (
                    exists (
                    select 1
                    from public.devices d
                    where d.id = device_status.device_id
                    and d.owner_id = (select auth.uid())
                    )
                    );

drop policy if exists "device_settings_select_own_device" on public.device_settings;
create policy "device_settings_select_own_device"
on public.device_settings
for select
                    to authenticated
                    using (
                    exists (
                    select 1
                    from public.devices d
                    where d.id = device_settings.device_id
                    and d.owner_id = (select auth.uid())
                    )
                    );

drop policy if exists "sensor_events_select_own_device" on public.sensor_events;
create policy "sensor_events_select_own_device"
on public.sensor_events
for select
                    to authenticated
                    using (
                    exists (
                    select 1
                    from public.devices d
                    where d.id = sensor_events.device_id
                    and d.owner_id = (select auth.uid())
                    )
                    );
