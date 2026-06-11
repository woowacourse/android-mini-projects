package a4.dogsignal.data.network

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class DeviceDataSource(
    private val supabase: SupabaseClient,
) {
    suspend fun getDeviceId(
        code: String,
        secret: String,
    ): String {
        return supabase.postgrest
            .rpc(
                function = "verify_device",
                parameters =
                    buildJsonObject {
                        put("p_device_code", code.trim())
                        put("p_device_key", secret.trim())
                    },
            ).decodeAsOrNull<String>()
            ?: error("디바이스 코드 또는 시크릿이 올바르지 않습니다.")
    }
}
