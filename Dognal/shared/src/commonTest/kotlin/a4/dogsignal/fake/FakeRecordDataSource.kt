package a4.dogsignal.fake

import a4.dogsignal.data.network.RecordDataSource
import a4.dogsignal.data.network.dto.CreateRecordDto
import a4.dogsignal.data.network.dto.RecordDto
import kotlinx.coroutines.CompletableDeferred

class FakeRecordDataSource : RecordDataSource {
    var records: List<RecordDto> = emptyList()

    var shouldThrowOnCreate = false
    var shouldThrowOnUpdate = false
    var shouldThrowOnDelete = false

    // createRecord를 특정 시점까지 블로킹하고 싶을 때 설정
    var createDeferred: CompletableDeferred<Unit>? = null

    var createCallCount = 0
    var capturedCreateNote: String? = null
    var capturedUpdateNote: String? = null
    var capturedFrom: kotlin.time.Instant? = null
    var capturedUntil: kotlin.time.Instant? = null

    override suspend fun getRecords(deviceId: String): List<RecordDto> = records

    override suspend fun getRecordsBetween(
        deviceId: String,
        from: kotlin.time.Instant,
        until: kotlin.time.Instant,
    ): List<RecordDto> {
        capturedFrom = from
        capturedUntil = until
        return records
    }

    override suspend fun createRecord(record: CreateRecordDto) {
        createCallCount++
        capturedCreateNote = record.note
        createDeferred?.await()
        if (shouldThrowOnCreate) throw RuntimeException("createRecord 실패")
    }

    override suspend fun updateRecord(
        id: String,
        recordType: String,
        occurredAt: kotlin.time.Instant,
        note: String,
    ) {
        capturedUpdateNote = note
        if (shouldThrowOnUpdate) throw RuntimeException("updateRecord 실패")
    }

    override suspend fun deleteRecord(id: String) {
        if (shouldThrowOnDelete) throw RuntimeException("deleteRecord 실패")
    }
}
