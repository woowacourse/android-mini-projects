package a4.dogsignal.data.network

import a4.dogsignal.data.network.dto.RecordDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order

class RecordDataSource(
    private val supabase: SupabaseClient,
) {
    suspend fun getRecords(userId: String): List<RecordDto> {
        return supabase.from("potty_records")
            .select(columns = Columns.list("id", "record_type", "occurred_at")) {
                filter {
                    eq("user_id", userId)
                }
                order("occurred_at", Order.DESCENDING)
                limit(50)
            }
            .decodeList<RecordDto>()
    }
}
