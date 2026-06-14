package a4.dogsignal.ui.common.component.picker

import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalDate

@Composable
internal expect fun DatePickerBottomSheet(
    selectedDate: LocalDate,
    maxDate: LocalDate?,
    onDateChange: (LocalDate) -> Unit,
    onDismissRequest: () -> Unit,
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit,
)
