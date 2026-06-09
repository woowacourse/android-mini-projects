package a4.dogsignal.ui.record

import a4.dogsignal.model.Record
import a4.dogsignal.ui.common.component.DognalTab

internal data class RecordUiState(
    val selectedTab: DognalTab,
    val dateLabel: String,
    val dateValue: String,
    val recordList: List<Record>,
    val isLoading: Boolean = false,
)
