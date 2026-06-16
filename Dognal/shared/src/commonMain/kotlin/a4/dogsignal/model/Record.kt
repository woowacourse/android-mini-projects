package a4.dogsignal.model

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDateTime

@Immutable
data class Record(
    val id: String,
    val type: RecordType,
    val dateTime: LocalDateTime,
    val note: String? = null,
)
