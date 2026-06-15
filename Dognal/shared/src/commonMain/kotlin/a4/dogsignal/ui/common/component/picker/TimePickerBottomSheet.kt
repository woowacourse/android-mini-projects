package a4.dogsignal.ui.common.component.picker

import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalTime

@Composable
internal expect fun TimePickerBottomSheet(
    selectedTime: LocalTime,
    maxTime: LocalTime?,
    onTimeChange: (LocalTime) -> Unit,
    onDismissRequest: () -> Unit,
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit,
)
