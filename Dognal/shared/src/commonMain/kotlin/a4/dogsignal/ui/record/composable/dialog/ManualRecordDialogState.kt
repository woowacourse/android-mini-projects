package a4.dogsignal.ui.record.composable.dialog

import a4.dogsignal.model.RecordType
import kotlinx.datetime.LocalDateTime

internal data class ManualRecordDialogState(
    val selectedRecordType: RecordType,
    val dateTime: LocalDateTime,
    val memo: String,
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
