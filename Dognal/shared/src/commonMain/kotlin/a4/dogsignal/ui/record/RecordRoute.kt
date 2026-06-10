package a4.dogsignal.ui.record

import a4.dogsignal.ui.common.component.DognalTab
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

    val hideManualRecordDialog = {
        isManualRecordDialogVisible = false
    }

    RecordScreen(
        state = state,
        isManualRecordDialogVisible = isManualRecordDialogVisible,
        manualRecordDialogState = manualRecordDialogState,
        onAddRecordClick = {
            manualRecordDialogState = ManualRecordDialogState.initial(currentDateTime())
            isManualRecordDialogVisible = true
        },
        onManualRecordDismiss = hideManualRecordDialog,
        onManualRecordTypeClick = { recordType ->
            manualRecordDialogState =
                manualRecordDialogState.copy(selectedRecordType = recordType)
        },
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
