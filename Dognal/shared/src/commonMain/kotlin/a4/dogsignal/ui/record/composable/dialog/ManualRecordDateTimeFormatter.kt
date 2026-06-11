package a4.dogsignal.ui.record.composable.dialog

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.number

internal object ManualRecordDateTimeFormatter {
    fun formatDate(date: LocalDate): String =
        "${date.year}.${date.month.number.toPaddedString()}.${date.day.toPaddedString()}"

    fun formatTime(time: LocalTime): String = "${time.hour.toPaddedString()}:${time.minute.toPaddedString()}"

    private fun Int.toPaddedString(): String = toString().padStart(2, '0')
}
