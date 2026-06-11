package a4.dogsignal.ui.common.component.picker

import android.app.TimePickerDialog
import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.datetime.LocalTime

@Composable
internal actual fun TimePickerBottomSheet(
    selectedTime: LocalTime,
    onTimeChange: (LocalTime) -> Unit,
    onDismissRequest: () -> Unit,
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    val currentOnTimeChange by rememberUpdatedState(onTimeChange)
    val currentOnConfirmClick by rememberUpdatedState(onConfirmClick)

    AndroidPickerDialogEffect(
        key = selectedTime,
        onDismissRequest = onDismissRequest,
        onCancelClick = onCancelClick,
    ) { context, handleDialogAction ->
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                handleDialogAction {
                    currentOnTimeChange(LocalTime(hour = hourOfDay, minute = minute))
                    currentOnConfirmClick()
                }
            },
            selectedTime.hour,
            selectedTime.minute,
            DateFormat.is24HourFormat(context),
        )
    }
}
