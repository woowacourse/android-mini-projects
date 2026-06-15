-- =====================================================================
-- 008_device_push_tokens.sql
-- FCM push token 저장용 테이블 + RPC
-- =====================================================================

create schema if not exists extensions;
create extension if not exists pgcrypto with schema extensions;

-- ---------------------------------------------------------------------
-- 1. 앱 설치 단위 FCM token 저장 테이블
-- ---------------------------------------------------------------------

create table if not exists public.device_push_tokens (
    id uuid primary key default extensions.gen_random_uuid(),

    device_id uuid not null
        references public.devices(id)
        on delete cascade,

    installation_id uuid not null,

    platform text not null
        check (platform in ('android', 'ios')),

    fcm_token text not null,

    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    last_seen_at timestamptz not null default now(),

    constraint device_push_tokens_installation_unique
        unique (device_id, platform, installation_id),

    constraint device_push_tokens_fcm_token_unique
        unique (fcm_token)
);

create index if not exists device_push_tokens_device_id_idx
on public.device_push_tokens(device_id);

create index if not exists device_push_tokens_platform_idx
on public.device_push_tokens(platform);

revoke all on table public.device_push_tokens from public;
revoke all on table public.device_push_tokens from anon;
revoke all on table public.device_push_tokens from authenticated;

-- ---------------------------------------------------------------------
-- 2. FCM token 등록 RPC
-- ---------------------------------------------------------------------

drop function if exists public.register_device_push_token(
    text,
    text,
    uuid,
    text,
    text
);

create function public.register_device_push_token(
    p_device_code text,
    p_device_key text,
    p_installation_id uuid,
    p_platform text,
    p_fcm_token text
)
returns uuid
language plpgsql
security definer
set search_path = public
as $$
declare
    v_device_id uuid;
    v_token_id uuid;
begin
    if p_device_code is null or length(trim(p_device_code)) = 0 then
        raise exception 'INVALID_DEVICE_CODE';
    end if;

    if p_device_key is null or length(trim(p_device_key)) = 0 then
        raise exception 'INVALID_DEVICE_KEY';
    end if;

    if p_installation_id is null then
        raise exception 'INVALID_INSTALLATION_ID';
    end if;

    if p_platform not in ('android', 'ios') then
        raise exception 'INVALID_PLATFORM';
    end if;

    if p_fcm_token is null or length(trim(p_fcm_token)) < 20 then
        raise exception 'INVALID_FCM_TOKEN';
    end if;

    v_device_id := public.verify_device(
        trim(p_device_code),
        trim(p_device_key)
    );

    if v_device_id is null then
        raise exception 'INVALID_DEVICE';
    end if;

    insert into public.device_push_tokens (
        device_id,
        installation_id,
        platform,
        fcm_token,
        created_at,
        updated_at,
        last_seen_at
    )
    values (
        v_device_id,
        p_installation_id,
        p_platform,
        trim(p_fcm_token),
        now(),
        now(),
        now()
    )
    on conflict (device_id, platform, installation_id)
    do update set
        fcm_token = excluded.fcm_token,
        updated_at = now(),
        last_seen_at = now()
    returning id into v_token_id;

    return v_token_id;
end;
$$;

revoke execute on function public.register_device_push_token(
    text,
    text,
    uuid,
    text,
    text
) from public;

revoke execute on function public.register_device_push_token(
    text,
    text,
    uuid,
    text,
    text
) from authenticated;

grant execute on function public.register_device_push_token(
    text,
    text,
    uuid,
    text,
    text
) to anon;

notify pgrst, 'reload schema';
