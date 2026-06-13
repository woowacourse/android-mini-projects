package a4.dogsignal.ui.record.composable.dialog

import a4.dogsignal.model.RecordType
import a4.dogsignal.ui.record.composable.dialog.composable.DialogActionButtons
import a4.dogsignal.ui.record.composable.dialog.composable.DialogDateTimeRow
import a4.dogsignal.ui.record.composable.dialog.composable.DialogHeader
import a4.dogsignal.ui.record.composable.dialog.composable.DialogMemoField
import a4.dogsignal.ui.record.composable.dialog.composable.DialogRecordTypeRow
import a4.dogsignal.ui.theme.AppTheme
import a4.dogsignal.ui.theme.ErrorRed
import a4.dogsignal.ui.theme.TextTertiary
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

@Composable
internal fun ManualRecordDialog(
    manualRecordDialogState: ManualRecordDialogState,
    isSaving: Boolean,
    errorMessage: String?,
    onDismissRequest: () -> Unit,
    onRecordTypeClick: (RecordType) -> Unit,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit,
    onMemoChange: (String) -> Unit,
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    Dialog(
        onDismissRequest = {
            focusManager.clearFocus()
            onDismissRequest()
        },
    ) {
        ManualRecordDialogContent(
            state = manualRecordDialogState,
            isSaving = isSaving,
            errorMessage = errorMessage,
            onRecordTypeClick = onRecordTypeClick,
            onDateClick = onDateClick,
            onTimeClick = onTimeClick,
            onMemoChange = onMemoChange,
            onCancelClick = onCancelClick,
            onSaveClick = onSaveClick,
            onDeleteClick = onDeleteClick,
            modifier = modifier,
        )
    }
}

@Composable
private fun ManualRecordDialogContent(
    state: ManualRecordDialogState,
    isSaving: Boolean,
    errorMessage: String?,
    onRecordTypeClick: (RecordType) -> Unit,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit,
    onMemoChange: (String) -> Unit,
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    Surface(
        modifier =
            modifier
                .fillMaxWidth()
                .imePadding()
                .pointerInput(Unit) {
                    detectTapGestures {
                        focusManager.clearFocus()
                    }
                },
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 22.dp, top = 7.dp, end = 22.dp, bottom = 16.dp),
        ) {
            Spacer(Modifier.height(29.dp))
            if (state.isEditing) {
                DialogHeader(
                    title = "기록 수정",
                    subtitle = "기록한 배변 내용을 수정해요",
                )
            } else {
                DialogHeader()
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "기록 유형",
                color = TextTertiary,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(29.dp))
            DialogRecordTypeRow(
                selectedRecordType = state.selectedRecordType,
                onRecordTypeClick = { recordType ->
                    focusManager.clearFocus()
                    onRecordTypeClick(recordType)
                },
            )
            Spacer(Modifier.height(27.dp))
            DialogDateTimeRow(
                dateTime = state.dateTime,
                onDateClick = {
                    focusManager.clearFocus()
                    onDateClick()
                },
                onTimeClick = {
                    focusManager.clearFocus()
                    onTimeClick()
                },
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = "메모",
                color = TextTertiary,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(14.dp))
            DialogMemoField(
                value = state.memo,
                onValueChange = onMemoChange,
            )
            if (errorMessage != null) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = errorMessage,
                    color = ErrorRed,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Spacer(Modifier.height(27.dp))
            DialogActionButtons(
                onCancelClick = {
                    focusManager.clearFocus()
                    onCancelClick()
                },
                onSaveClick = {
                    focusManager.clearFocus()
                    onSaveClick()
                },
                onDeleteClick = {
                    focusManager.clearFocus()
                    onDeleteClick()
                },
                isSaveEnabled = !isSaving,
                isEditing = state.isEditing,
                saveText = when {
                    isSaving -> "저장 중..."
                    state.isEditing -> "수정 완료"
                    else -> "기록 저장"
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ManualRecordDialogContentPreview() {
    AppTheme {
        ManualRecordDialogContent(
            state =
                ManualRecordDialogState(
                    selectedRecordType = RecordType.URINE,
                    dateTime =
                        LocalDateTime(
                            date = LocalDate(2026, 6, 4),
                            time = LocalTime(15, 40),
                        ),
                    memo = "",
                ),
            isSaving = false,
            errorMessage = null,
            onRecordTypeClick = {},
            onDateClick = {},
            onTimeClick = {},
            onMemoChange = {},
            onCancelClick = {},
            onSaveClick = {},
            onDeleteClick = {},
        )
    }
}
