package a4.dogsignal.ui.record.composable.dialog

import a4.dogsignal.model.RecordType
import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDateTime

@Immutable
internal data class ManualRecordDialogState(
    val selectedRecordType: RecordType,
    val dateTime: LocalDateTime,
    val memo: String,
    val isEditing: Boolean = false,
) {
    companion object {
        fun initial(dateTime: LocalDateTime): ManualRecordDialogState =
            ManualRecordDialogState(
                selectedRecordType = RecordType.URINE,
                dateTime = dateTime,
                memo = "",
            )
    }
}
