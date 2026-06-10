package a4.dogsignal.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class RecordDto(
    val id: String,
    @SerialName("record_type") val recordType: String,
    @SerialName("occurred_at") val occurredAt: Instant,
)

@Serializable
data class CreateRecordDto(
    @SerialName("device_id") val deviceId: String,
    @SerialName("record_type") val recordType: String,
    val source: String,
    @SerialName("occurred_at") val occurredAt: Instant,
    val note: String,
    @SerialName("created_at") val createdAt: Instant,
)
