package a4.dogsignal.ui.common.component.picker

import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalDate
import platform.UIKit.UIDatePickerMode

@Composable
internal actual fun DatePickerBottomSheet(
    selectedDate: LocalDate,
    maxDate: LocalDate?,
    onDateChange: (LocalDate) -> Unit,
    onDismissRequest: () -> Unit,
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    IosNativePickerDialog(
        title = "날짜 선택",
        date = selectedDate.toNSDate(),
        mode = UIDatePickerMode.UIDatePickerModeDate,
        maximumDate = maxDate?.toNSDate(),
        onDismissRequest = onDismissRequest,
        onCancelClick = onCancelClick,
        onConfirmClick = { date ->
            onDateChange(date.toLocalDate())
            onConfirmClick()
        },
    )
}
