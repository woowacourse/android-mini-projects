package a4.dogsignal.ui.common.component.picker

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.number
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.NSDateComponents

internal fun LocalDate.toNSDate(): NSDate {
    val components =
        NSDateComponents().apply {
            year = this@toNSDate.year.toLong()
            month = this@toNSDate.month.number.toLong()
            day = this@toNSDate.day.toLong()
            hour = NOON_HOUR.toLong()
        }
    return NSCalendar.currentCalendar.dateFromComponents(components) ?: NSDate()
}

internal fun NSDate.toLocalDate(): LocalDate {
    val components =
        NSCalendar.currentCalendar.components(
            NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay,
            fromDate = this,
        )
    return LocalDate(
        components.year.toInt(),
        components.month.toInt().toMonth(),
        components.day.toInt(),
    )
}

internal fun LocalTime.toNSDate(): NSDate {
    val components =
        NSCalendar.currentCalendar.components(
            NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay,
            fromDate = NSDate(),
        )
    components.hour = hour.toLong()
    components.minute = minute.toLong()
    return NSCalendar.currentCalendar.dateFromComponents(components) ?: NSDate()
}

internal fun NSDate.toLocalTime(): LocalTime {
    val components =
        NSCalendar.currentCalendar.components(
            NSCalendarUnitHour or NSCalendarUnitMinute,
            fromDate = this,
        )
    return LocalTime(
        hour = components.hour.toInt(),
        minute = components.minute.toInt(),
    )
}

private fun Int.toMonth(): Month = Month.entries.first { it.number == this }

private const val NOON_HOUR = 12
