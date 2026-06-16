package a4.dogsignal.data.network

import a4.dogsignal.data.network.dto.CreateRecordDto
import a4.dogsignal.data.network.dto.RecordDto

interface RecordDataSource {
    suspend fun getRecords(deviceId: String): List<RecordDto>

    suspend fun getRecordsBetween(
        deviceId: String,
        from: kotlin.time.Instant,
        until: kotlin.time.Instant,
    ): List<RecordDto>

    suspend fun createRecord(record: CreateRecordDto)

    suspend fun updateRecord(
        id: String,
        recordType: String,
        occurredAt: kotlin.time.Instant,
        note: String,
    )

    suspend fun deleteRecord(id: String)
}
