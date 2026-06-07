package a4.dogsignal.ui.record

import a4.dogsignal.model.Record
import a4.dogsignal.ui.common.component.DognalTab
import kotlinx.datetime.LocalDate

internal data class RecordUiState(
    val selectedTab: DognalTab,
    val date: LocalDate,
    val recordList: List<Record>,
)
