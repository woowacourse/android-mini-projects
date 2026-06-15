package a4.dogsignal.ui.record.composable.dialog

import a4.dogsignal.model.Record
import a4.dogsignal.model.RecordType
import kotlinx.datetime.LocalDateTime

internal data class ManualRecordDialogState(
    val selectedRecordType: RecordType,
    val dateTime: LocalDateTime,
    val memo: String,
    val editingRecordId: String? = null,
) {
    val isEditing: Boolean get() = editingRecordId != null

    companion object {
        fun initial(dateTime: LocalDateTime): ManualRecordDialogState =
            ManualRecordDialogState(
                selectedRecordType = RecordType.URINE,
                dateTime = dateTime,
                memo = "",
            )

        fun fromRecord(record: Record): ManualRecordDialogState =
            ManualRecordDialogState(
                selectedRecordType = record.type,
                dateTime = record.dateTime,
                memo = record.note ?: "",
                editingRecordId = record.id,
            )
    }
}
