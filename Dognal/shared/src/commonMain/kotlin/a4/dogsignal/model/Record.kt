package a4.dogsignal.model

import kotlinx.datetime.LocalDateTime

data class Record(
    val id: String,
    val type: RecordType,
    val dateTime: LocalDateTime,
)
