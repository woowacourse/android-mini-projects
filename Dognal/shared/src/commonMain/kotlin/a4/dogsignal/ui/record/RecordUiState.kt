package a4.dogsignal.ui.record

import a4.dogsignal.model.Record
import kotlinx.datetime.LocalDate

internal data class RecordUiState(
    val tabs: List<String>,
    val selectedTabIndex: Int,
    val date: LocalDate,
    val recordList: List<Record>,
)
