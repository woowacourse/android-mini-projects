package a4.dogsignal.ui.common.component.picker

import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalTime
import platform.UIKit.UIDatePickerMode

@Composable
internal actual fun TimePickerBottomSheet(
    selectedTime: LocalTime,
    maxTime: LocalTime?,
    onTimeChange: (LocalTime) -> Unit,
    onDismissRequest: () -> Unit,
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    IosNativePickerDialog(
        title = "시간 선택",
        date = selectedTime.toNSDate(),
        mode = UIDatePickerMode.UIDatePickerModeTime,
        maximumDate = maxTime?.toNSDate(),
        onDismissRequest = onDismissRequest,
        onCancelClick = onCancelClick,
        onConfirmClick = { date ->
            onTimeChange(date.toLocalTime())
            onConfirmClick()
        },
    )
}
