package a4.dogsignal.ui.home

import a4.dogsignal.model.RecordType

internal data class HomeUiState(
    val title: String,
    val subtitle: String,
    val tabs: List<String>,
    val selectedTabIndex: Int,
    val statusCard: HomeStatusCardState,
    val summaryCards: List<HomeSummaryCardState>,
    val actionLabel: String,
)

internal data class HomeStatusCardState(
    val title: String,
    val description: String,
)

internal data class HomeSummaryCardState(
    val recordType: RecordType,
    val count: Int,
)
