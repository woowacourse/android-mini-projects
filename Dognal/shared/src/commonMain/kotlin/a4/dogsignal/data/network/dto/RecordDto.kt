package a4.dogsignal.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class RecordDto(
    val id: String,
    @SerialName("record_type") val recordType: String,
    @SerialName("occurred_at") val occurredAt: Instant,
    val note: String? = null,
)
