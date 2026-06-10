package a4.dogsignal.data.network

import a4.dogsignal.data.network.dto.RecordDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order

class RecordDataSource(
    private val supabase: SupabaseClient,
) {
    suspend fun getRecords(deviceId: String): List<RecordDto> {
        return supabase.from("potty_records")
            .select(columns = Columns.list("id", "record_type", "occurred_at", "note")) {
                filter {
                    eq("device_id", deviceId)
                }
                order("occurred_at", Order.DESCENDING)
                limit(50)
            }
            .decodeList<RecordDto>()
    }

    suspend fun getRecordsBetween(
        deviceId: String,
        from: kotlin.time.Instant,
        until: kotlin.time.Instant,
    ): List<RecordDto> {
        return supabase.from("potty_records")
            .select(columns = Columns.list("id", "record_type", "occurred_at", "note")) {
                filter {
                    eq("device_id", deviceId)
                    gte("occurred_at", from.toString())
                    lt("occurred_at", until.toString())
                }
                order("occurred_at", Order.DESCENDING)
            }
            .decodeList<RecordDto>()
    }
}
