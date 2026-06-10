package a4.dogsignal.data.repository

import a4.dogsignal.data.network.RecordDataSource
import a4.dogsignal.data.network.dto.CreateRecordDto
import a4.dogsignal.data.network.dto.RecordDto
import a4.dogsignal.model.Record
import a4.dogsignal.model.RecordType
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class RecordRepository(
    private val dataSource: RecordDataSource,
) {
    suspend fun getRecords(deviceId: String): List<Record> {
        return dataSource.getRecords(deviceId)
            .map { it.toDomain() }
    }

    suspend fun getTodayRecords(deviceId: String): List<Record> {
        val tz = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(tz).date
        val from = today.atStartOfDayIn(tz)
        val until = today.plus(1, DateTimeUnit.DAY).atStartOfDayIn(tz)
        return dataSource.getRecordsBetween(deviceId, from, until).map { it.toDomain() }
    }

    suspend fun createManualRecord(
        deviceId: String,
        type: RecordType,
        dateTime: LocalDateTime,
        memo: String,
    ) {
        val record =
            CreateRecordDto(
                deviceId = deviceId,
                recordType = type.toRecordTypeColumn(),
                source = MANUAL_RECORD_SOURCE,
                occurredAt = dateTime.toInstant(TimeZone.currentSystemDefault()),
                note = memo.trim(),
                createdAt = Clock.System.now(),
            )
        dataSource.createRecord(record)
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

private fun RecordType.toRecordTypeColumn(): String =
    when (this) {
        RecordType.PAD -> "VISIT"
        RecordType.URINE -> "URINE"
        RecordType.STOOL -> "STOOL"
    }

private const val MANUAL_RECORD_SOURCE = "USER_WRITE"
