package a4.dogsignal.ui.record

import a4.dogsignal.ui.common.component.DognalTab
import a4.dogsignal.ui.record.composable.dialog.ManualRecordDateTimePickerState
import a4.dogsignal.ui.record.composable.dialog.ManualRecordDialogState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

@Composable
internal fun RecordRoute(
    viewModel: RecordViewModel,
    onTabClick: (DognalTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var isManualRecordDialogVisible by remember { mutableStateOf(false) }
    var manualRecordDialogState by remember {
        mutableStateOf(ManualRecordDialogState.initial(currentDateTime()))
    }
    var manualRecordDateTimePickerState by remember {
        mutableStateOf<ManualRecordDateTimePickerState?>(null)
    }

    val hideManualRecordDialog = {
        isManualRecordDialogVisible = false
        manualRecordDateTimePickerState = null
    }

    val hideManualRecordDateTimePicker = {
        manualRecordDateTimePickerState = null
    }

    val confirmManualRecordDateTimePicker = {
        manualRecordDateTimePickerState?.let { pickerState ->
            manualRecordDialogState =
                manualRecordDialogState.copy(dateTime = pickerState.dateTime)
        }
        manualRecordDateTimePickerState = null
    }

    RecordScreen(
        state = state,
        isManualRecordDialogVisible = isManualRecordDialogVisible,
        manualRecordDialogState = manualRecordDialogState,
        manualRecordDateTimePickerState = manualRecordDateTimePickerState,
        onAddRecordClick = {
            manualRecordDialogState = ManualRecordDialogState.initial(currentDateTime())
            manualRecordDateTimePickerState = null
            isManualRecordDialogVisible = true
        },
        onManualRecordDismiss = hideManualRecordDialog,
        onManualRecordTypeClick = { recordType ->
            manualRecordDialogState =
                manualRecordDialogState.copy(selectedRecordType = recordType)
        },
        onManualRecordDateClick = {
            manualRecordDateTimePickerState =
                ManualRecordDateTimePickerState.date(manualRecordDialogState.dateTime)
        },
        onManualRecordTimeClick = {
            manualRecordDateTimePickerState =
                ManualRecordDateTimePickerState.time(manualRecordDialogState.dateTime)
        },
        onManualRecordPickerDateChange = { date ->
            manualRecordDateTimePickerState =
                manualRecordDateTimePickerState?.updateDate(date)
        },
        onManualRecordPickerTimeChange = { time ->
            manualRecordDateTimePickerState =
                manualRecordDateTimePickerState?.updateTime(time)
        },
        onManualRecordPickerDismiss = hideManualRecordDateTimePicker,
        onManualRecordPickerConfirm = confirmManualRecordDateTimePicker,
        onManualRecordMemoChange = { memo ->
            manualRecordDialogState =
                manualRecordDialogState.copy(memo = memo)
        },
        onManualRecordSaveClick = hideManualRecordDialog,
        onTabClick = onTabClick,
        modifier = modifier,
    )
}

private fun currentDateTime(): LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
