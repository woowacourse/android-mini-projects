package a4.dogsignal.ui.record.composable.dialog

import a4.dogsignal.ui.common.component.picker.DatePickerBottomSheet
import a4.dogsignal.ui.common.component.picker.TimePickerBottomSheet
import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@Composable
internal fun ManualRecordDateTimePicker(
    state: ManualRecordDateTimePickerState?,
    onDateChange: (LocalDate) -> Unit,
    onTimeChange: (LocalTime) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    state ?: return

    when (state.type) {
        ManualRecordDateTimePickerType.DATE ->
            DatePickerBottomSheet(
                selectedDate = state.dateTime.date,
                onDateChange = onDateChange,
                onDismissRequest = onDismissRequest,
                onCancelClick = onDismissRequest,
                onConfirmClick = onConfirmClick,
            )

        ManualRecordDateTimePickerType.TIME ->
            TimePickerBottomSheet(
                selectedTime = state.dateTime.time,
                onTimeChange = onTimeChange,
                onDismissRequest = onDismissRequest,
                onCancelClick = onDismissRequest,
                onConfirmClick = onConfirmClick,
            )
    }
}
