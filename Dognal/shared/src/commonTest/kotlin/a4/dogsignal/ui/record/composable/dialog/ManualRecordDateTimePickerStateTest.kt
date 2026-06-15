@file:Suppress("NonAsciiCharacters")

package a4.dogsignal.ui.record.composable.dialog

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class ManualRecordDateTimePickerStateTest {

    private val maxDateTime = LocalDateTime(2026, 6, 15, 14, 0) // 오늘 14:00

    @Test
    fun `updateDate 후 결과가 maxDateTime을 초과하면 maxDateTime으로 고정된다`() {
        val state = ManualRecordDateTimePickerState.date(
            dateTime = LocalDateTime(2026, 6, 14, 23, 0), // 어제 23:00
            maxDateTime = maxDateTime,
        )

        val updated = state.updateDate(LocalDate(2026, 6, 15)) // 날짜를 오늘로 변경
        assertEquals(maxDateTime, updated.dateTime)
    }

    @Test
    fun `updateDate 후 결과가 maxDateTime 이하이면 시간이 유지된다`() {
        val state = ManualRecordDateTimePickerState.date(
            dateTime = LocalDateTime(2026, 6, 14, 10, 0), // 어제 10:00
            maxDateTime = maxDateTime,
        )

        val updated = state.updateDate(LocalDate(2026, 6, 15)) // 날짜를 오늘로 변경

        assertEquals(LocalDateTime(2026, 6, 15, 10, 0), updated.dateTime)
    }

    @Test
    fun `updateDate 후 결과가 maxDateTime과 같으면 그대로 유지된다`() {
        val state = ManualRecordDateTimePickerState.date(
            dateTime = LocalDateTime(2026, 6, 14, 14, 0), // 어제 14:00
            maxDateTime = maxDateTime,
        )

        val updated = state.updateDate(LocalDate(2026, 6, 15))

        assertEquals(maxDateTime, updated.dateTime)
    }

    @Test
    fun `과거 날짜로 변경하면 maxDateTime 제한 없이 시간이 유지된다`() {
        val state = ManualRecordDateTimePickerState.date(
            dateTime = LocalDateTime(2026, 6, 15, 13, 0),
            maxDateTime = maxDateTime,
        )

        val updated = state.updateDate(LocalDate(2026, 6, 13)) // 이틀 전으로 변경

        assertEquals(LocalDateTime(2026, 6, 13, 13, 0), updated.dateTime)
    }

    @Test
    fun `updateTime은 날짜를 변경하지 않는다`() {
        val state = ManualRecordDateTimePickerState.time(
            dateTime = LocalDateTime(2026, 6, 14, 10, 0),
            maxDateTime = maxDateTime,
        )

        val updated = state.updateTime(kotlinx.datetime.LocalTime(8, 30))

        assertEquals(LocalDate(2026, 6, 14), updated.dateTime.date)
        assertEquals(kotlinx.datetime.LocalTime(8, 30), updated.dateTime.time)
    }
}
