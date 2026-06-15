package a4.dogsignal.data.network

import a4.dogsignal.data.network.dto.CreateRecordDto
import a4.dogsignal.data.network.dto.RecordDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

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

    suspend fun createRecord(record: CreateRecordDto) {
        supabase.from("potty_records")
            .insert(record.toJsonBody()) {
                defaultToNull = false
            }
    }

    suspend fun updateRecord(
        id: String,
        recordType: String,
        occurredAt: kotlin.time.Instant,
        note: String,
    ) {
        supabase.from("potty_records")
            .update(
                buildJsonObject {
                    put("record_type", recordType)
                    put("occurred_at", occurredAt.toString())
                    put("note", note)
                },
            ) {
                filter { eq("id", id) }
            }
    }

    suspend fun deleteRecord(id: String) {
        supabase.from("potty_records")
            .delete {
                filter { eq("id", id) }
            }
    }
}

private fun CreateRecordDto.toJsonBody() =
    buildJsonArray {
        add(
            buildJsonObject {
                put("device_id", deviceId)
                put("record_type", recordType)
                put("source", source)
                put("occurred_at", occurredAt.toString())
                put("note", note)
                put("created_at", createdAt.toString())
            },
        )
    }
