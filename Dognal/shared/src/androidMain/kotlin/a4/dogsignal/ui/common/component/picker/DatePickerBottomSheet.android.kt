package a4.dogsignal.ui.common.component.picker

import android.app.DatePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.number

@Composable
internal actual fun DatePickerBottomSheet(
    selectedDate: LocalDate,
    maxDate: LocalDate?,
    onDateChange: (LocalDate) -> Unit,
    onDismissRequest: () -> Unit,
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    val currentOnDateChange by rememberUpdatedState(onDateChange)
    val currentOnConfirmClick by rememberUpdatedState(onConfirmClick)

    AndroidPickerDialogEffect(
        key = selectedDate,
        onDismissRequest = onDismissRequest,
        onCancelClick = onCancelClick,
    ) { context, handleDialogAction ->
        DatePickerDialog(
            context,
            { _, year, monthIndex, dayOfMonth ->
                handleDialogAction {
                    currentOnDateChange(LocalDate(year, (monthIndex + 1).toMonth(), dayOfMonth))
                    currentOnConfirmClick()
                }
            },
            selectedDate.year,
            selectedDate.month.number - 1,
            selectedDate.day,
        ).also { dialog ->
            if (maxDate != null) {
                val cal = java.util.Calendar.getInstance().apply {
                    set(maxDate.year, maxDate.month.number - 1, maxDate.day)
                    set(java.util.Calendar.HOUR_OF_DAY, 23)
                    set(java.util.Calendar.MINUTE, 59)
                    set(java.util.Calendar.SECOND, 59)
                    set(java.util.Calendar.MILLISECOND, 999)
                }
                dialog.datePicker.maxDate = cal.timeInMillis
            }
        }
    }
}

private fun Int.toMonth(): Month = Month.entries.first { it.number == this }
