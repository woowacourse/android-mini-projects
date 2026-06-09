package a4.dogsignal.di

import a4.dogsignal.BuildKonfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

fun createSupabase(): SupabaseClient =
    createSupabaseClient(
        supabaseUrl = BuildKonfig.SUPABASE_URL,
        supabaseKey = BuildKonfig.SUPABASE_KEY,
    ) {
        install(Postgrest)
        install(Realtime)
        install(Auth)
    }
