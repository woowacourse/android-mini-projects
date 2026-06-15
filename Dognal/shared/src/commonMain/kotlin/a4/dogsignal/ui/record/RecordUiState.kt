package a4.dogsignal.ui.record

import a4.dogsignal.model.Record
import a4.dogsignal.ui.common.component.DognalTab
import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
internal data class RecordUiState(
    val selectedTab: DognalTab,
    val dateLabel: String,
    val dateValue: String,
    val recordList: ImmutableList<Record>,
    val isLoading: Boolean = false,
    val isSavingManualRecord: Boolean = false,
    val manualRecordErrorMessage: String? = null,
)
