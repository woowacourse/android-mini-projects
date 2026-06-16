@file:Suppress("NonAsciiCharacters")

package a4.dogsignal.data.repository

import a4.dogsignal.fake.FakeRecordDataSource
import a4.dogsignal.model.RecordType
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Clock

class RecordRepositoryTest {
    private val fakeDataSource = FakeRecordDataSource()
    private val repository = RecordRepository(fakeDataSource)

    @Test
    fun `getTodayRecords는 오늘 자정부터 내일 자정 직전까지 요청한다`() = runTest {
        repository.getTodayRecords("device-1")

        val tz = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(tz).date
        val expectedFrom = today.atStartOfDayIn(tz)
        val expectedUntil = today.plus(1, DateTimeUnit.DAY).atStartOfDayIn(tz)

        assertEquals(expectedFrom, fakeDataSource.capturedFrom)
        assertEquals(expectedUntil, fakeDataSource.capturedUntil)
    }

    @Test
    fun `getTodayRecords의 from과 until은 정확히 하루 차이다`() = runTest {
        repository.getTodayRecords("device-1")

        val from = fakeDataSource.capturedFrom!!
        val until = fakeDataSource.capturedUntil!!
        val diffHours = (until - from).inWholeHours

        assertTrue(
            diffHours == 23L || diffHours == 24L || diffHours == 25L, // DST 보정 허용
            "from과 until의 차이는 하루여야 한다 (실제: ${diffHours}시간)",
        )
    }

    @Test
    fun `getTodayRecords의 from은 시간이 00_00이다`() = runTest {
        repository.getTodayRecords("device-1")

        val tz = TimeZone.currentSystemDefault()
        val fromLocal: LocalDateTime = fakeDataSource.capturedFrom!!.toLocalDateTime(tz)

        assertEquals(0, fromLocal.hour)
        assertEquals(0, fromLocal.minute)
        assertEquals(0, fromLocal.second)
    }

    @Test
    fun `createManualRecord는 메모 앞뒤 공백을 제거해서 저장한다`() = runTest {
        repository.createManualRecord(
            deviceId = "device-1",
            type = RecordType.URINE,
            dateTime = LocalDateTime(2026, 6, 15, 10, 0),
            memo = "  메모  ",
        )

        assertEquals("메모", fakeDataSource.capturedCreateNote)
    }

    @Test
    fun `updateRecord는 메모 앞뒤 공백을 제거해서 저장한다`() = runTest {
        repository.updateRecord(
            id = "record-1",
            type = RecordType.URINE,
            dateTime = LocalDateTime(2026, 6, 15, 10, 0),
            memo = "  수정된 메모  ",
        )

        assertEquals("수정된 메모", fakeDataSource.capturedUpdateNote)
    }
}
