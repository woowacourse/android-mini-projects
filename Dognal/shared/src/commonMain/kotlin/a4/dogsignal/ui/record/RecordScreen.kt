package a4.dogsignal.ui.record

import a4.dogsignal.model.Record
import a4.dogsignal.model.RecordType
import a4.dogsignal.ui.common.component.DognalTab
import a4.dogsignal.ui.common.component.DognalTabs
import a4.dogsignal.ui.common.component.ScreenHeader
import a4.dogsignal.ui.record.composable.AddRecordButton
import a4.dogsignal.ui.record.composable.DateHeaderCard
import a4.dogsignal.ui.record.composable.RecordTimelineGroup
import a4.dogsignal.ui.record.composable.dialog.ManualRecordDateTimePicker
import a4.dogsignal.ui.record.composable.dialog.ManualRecordDateTimePickerState
import a4.dogsignal.ui.record.composable.dialog.ManualRecordDialog
import a4.dogsignal.ui.record.composable.dialog.ManualRecordDialogState
import a4.dogsignal.ui.theme.AppTheme
import a4.dogsignal.ui.theme.RecordScreenBackground
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

@Composable
internal fun RecordScreen(
    state: RecordUiState,
    isManualRecordDialogVisible: Boolean,
    manualRecordDialogState: ManualRecordDialogState,
    manualRecordDateTimePickerState: ManualRecordDateTimePickerState?,
    onAddRecordClick: () -> Unit,
    onManualRecordDismiss: () -> Unit,
    onManualRecordTypeClick: (RecordType) -> Unit,
    onManualRecordDateClick: () -> Unit,
    onManualRecordTimeClick: () -> Unit,
    onManualRecordPickerDateChange: (LocalDate) -> Unit,
    onManualRecordPickerTimeChange: (LocalTime) -> Unit,
    onManualRecordPickerDismiss: () -> Unit,
    onManualRecordPickerConfirm: () -> Unit,
    onManualRecordMemoChange: (String) -> Unit,
    onManualRecordSaveClick: () -> Unit,
    onTabClick: (DognalTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = RecordScreenBackground,
        bottomBar = {
            AddRecordButton(
                modifier =
                    Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 38.dp),
                onClick = onAddRecordClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier.padding(innerPadding).padding(horizontal = 31.dp),
        ) {
            ScreenHeader(
                title = "배변 기록",
                subtitle = "수정 가능한 타임라인",
                modifier = Modifier.padding(top = 28.dp),
            )
            Spacer(Modifier.height(28.dp))
            DognalTabs(
                selectedTab = state.selectedTab,
                onTabClick = onTabClick,
                modifier = Modifier.align(Alignment.CenterHorizontally).width(250.dp),
            )
            Spacer(Modifier.height(30.dp))
            DateHeaderCard(
                label = state.dateLabel,
                date = state.dateValue,
            )
            Spacer(Modifier.height(32.dp))
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else {
                RecordTimelineGroup(
                    recordList = state.recordList,
                    onEditClick = {},
                    modifier = Modifier.padding(start = 15.dp),
                )
            }
        }
    }
    if (isManualRecordDialogVisible) {
        ManualRecordDialog(
            manualRecordDialogState = manualRecordDialogState,
            isSaving = state.isSavingManualRecord,
            errorMessage = state.manualRecordErrorMessage,
            onDismissRequest = onManualRecordDismiss,
            onRecordTypeClick = onManualRecordTypeClick,
            onDateClick = onManualRecordDateClick,
            onTimeClick = onManualRecordTimeClick,
            onMemoChange = onManualRecordMemoChange,
            onCancelClick = onManualRecordDismiss,
            onSaveClick = onManualRecordSaveClick,
        )
    }
    ManualRecordDateTimePicker(
        state = manualRecordDateTimePickerState,
        onDateChange = onManualRecordPickerDateChange,
        onTimeChange = onManualRecordPickerTimeChange,
        onDismissRequest = onManualRecordPickerDismiss,
        onConfirmClick = onManualRecordPickerConfirm,
    )
}

@Preview(showBackground = true)
@Composable
private fun RecordScreenPreview() {
    val date = LocalDate(2026, 6, 5)
    AppTheme {
        RecordScreen(
            state =
                RecordUiState(
                    selectedTab = DognalTab.RECORD,
                    dateLabel = "오늘",
                    dateValue = date.toString(),
                    recordList =
                        listOf(
                            Record(
                                id = "1",
                                dateTime = LocalDateTime(date, LocalTime(2, 5)),
                                type = RecordType.PAD,
                                note = "힘겨워 보였음"
                            ),
                            Record(
                                id = "2",
                                dateTime = LocalDateTime(date, LocalTime(1, 20)),
                                type = RecordType.URINE,
                            ),
                            Record(
                                id = "3",
                                dateTime = LocalDateTime(date, LocalTime(1, 20)),
                                type = RecordType.URINE,
                                note = "양 많음"
                            ),
                            Record(
                                id = "4",
                                dateTime = LocalDateTime(date, LocalTime(1, 20)),
                                type = RecordType.STOOL,
                            ),
                            Record(
                                id = "5",
                                dateTime = LocalDateTime(date, LocalTime(1, 20)),
                                type = RecordType.URINE,
                            ),
                            Record(
                                id = "6",
                                dateTime = LocalDateTime(date, LocalTime(1, 20)),
                                type = RecordType.URINE,
                            ),
                            Record(
                                id = "7",
                                dateTime = LocalDateTime(date, LocalTime(1, 20)),
                                type = RecordType.URINE,
                            ),
                        ),
                ),
            isManualRecordDialogVisible = false,
            manualRecordDialogState =
                ManualRecordDialogState(
                    selectedRecordType = RecordType.URINE,
                    dateTime = LocalDateTime(date, LocalTime(15, 40)),
                    memo = "",
                ),
            manualRecordDateTimePickerState = null,
            onAddRecordClick = {},
            onManualRecordDismiss = {},
            onManualRecordTypeClick = {},
            onManualRecordDateClick = {},
            onManualRecordTimeClick = {},
            onManualRecordPickerDateChange = {},
            onManualRecordPickerTimeChange = {},
            onManualRecordPickerDismiss = {},
            onManualRecordPickerConfirm = {},
            onManualRecordMemoChange = {},
            onManualRecordSaveClick = {},
            onTabClick = {},
        )
    }
}
