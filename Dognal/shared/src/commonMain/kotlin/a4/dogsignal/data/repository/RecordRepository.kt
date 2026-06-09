package a4.dogsignal.data.repository

import a4.dogsignal.data.network.RecordDataSource
import a4.dogsignal.data.network.dto.RecordDto
import a4.dogsignal.model.Record
import a4.dogsignal.model.RecordType
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class RecordRepository(
    private val dataSource: RecordDataSource,
) {
    suspend fun getRecords(userId: String): List<Record> {
        return dataSource.getRecords(userId)
            .map { it.toDomain() }
    }

    suspend fun getTodayRecords(userId: String): List<Record> {
        val tz = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(tz).date
        val from = today.atStartOfDayIn(tz)
        val until = today.plus(1, DateTimeUnit.DAY).atStartOfDayIn(tz)
        return dataSource.getRecordsBetween(userId, from, until).map { it.toDomain() }
    }
}

private fun RecordDto.toDomain(): Record =
    Record(
        id = id,
        type = recordType.toRecordType(),
        dateTime = occurredAt.toLocalDateTime(TimeZone.currentSystemDefault()),
    )

private fun String.toRecordType(): RecordType =
    when (this) {
        "VISIT" -> RecordType.PAD
        "URINE" -> RecordType.URINE
        "STOOL" -> RecordType.STOOL
        else -> error("Unknown record_type: $this")
    }
