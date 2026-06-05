package a4.dogsignal.model

import kotlinx.datetime.LocalDateTime

data class Record(
    val dateTime: LocalDateTime,
    val type: RecordType
)
