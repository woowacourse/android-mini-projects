package a4.dogsignal.ui.record.composable.dialog

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

@Immutable
internal data class ManualRecordDateTimePickerState(
    val type: ManualRecordDateTimePickerType,
    val dateTime: LocalDateTime,
    val maxDateTime: LocalDateTime,
) {
    fun updateDate(date: LocalDate): ManualRecordDateTimePickerState {
        val newDateTime = LocalDateTime(date = date, time = dateTime.time)
        return copy(dateTime = minOf(newDateTime, maxDateTime))
    }

    fun updateTime(time: LocalTime): ManualRecordDateTimePickerState =
        copy(dateTime = LocalDateTime(date = dateTime.date, time = time))

    companion object {
        fun date(dateTime: LocalDateTime, maxDateTime: LocalDateTime): ManualRecordDateTimePickerState =
            ManualRecordDateTimePickerState(
                type = ManualRecordDateTimePickerType.DATE,
                dateTime = dateTime,
                maxDateTime = maxDateTime,
            )

        fun time(dateTime: LocalDateTime, maxDateTime: LocalDateTime): ManualRecordDateTimePickerState =
            ManualRecordDateTimePickerState(
                type = ManualRecordDateTimePickerType.TIME,
                dateTime = dateTime,
                maxDateTime = maxDateTime,
            )
    }
}

internal enum class ManualRecordDateTimePickerType {
    DATE,
    TIME,
}
