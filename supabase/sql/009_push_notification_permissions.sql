-- =====================================================================
-- 009_push_notification_permissions.sql
-- Edge Function이 device_push_tokens를 조회/정리할 수 있도록 service_role 권한 명시
-- =====================================================================

grant select, delete on table public.device_push_tokens to service_role;
