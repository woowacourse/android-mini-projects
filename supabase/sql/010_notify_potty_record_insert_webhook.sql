-- =====================================================================
-- 010_notify_potty_record_insert_webhook.sql
-- potty_records INSERT 시 Edge Function으로 알림 발송 요청
--
-- 실행 전 반드시 replace-with-webhook-secret 값을 Supabase Edge Function
-- secret WEBHOOK_SECRET과 같은 값으로 바꾼다.
-- =====================================================================

create extension if not exists pg_net;

create or replace function public.notify_potty_record_insert_webhook()
returns trigger
language plpgsql
security definer
set search_path = public, net
as $$
declare
    request_id bigint;
begin
    select net.http_post(
        url := 'https://scacdglkduegrohbkcak.supabase.co/functions/v1/notify-potty-record-insert',
        body := jsonb_build_object(
            'type', TG_OP,
            'table', TG_TABLE_NAME,
            'schema', TG_TABLE_SCHEMA,
            'record', to_jsonb(new),
            'old_record', null
        ),
        headers := jsonb_build_object(
            'Content-Type', 'application/json',
            'x-webhook-secret', 'replace-with-webhook-secret'
        ),
        timeout_milliseconds := 5000
    )
    into request_id;

    return new;
end;
$$;

drop trigger if exists trg_notify_potty_record_insert
on public.potty_records;

create trigger trg_notify_potty_record_insert
after insert on public.potty_records
for each row
execute function public.notify_potty_record_insert_webhook();
