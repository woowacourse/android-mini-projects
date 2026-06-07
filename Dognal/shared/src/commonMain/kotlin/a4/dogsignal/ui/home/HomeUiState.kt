package a4.dogsignal.ui.home

import a4.dogsignal.model.RecordType
import a4.dogsignal.ui.common.component.DognalTab

internal data class HomeUiState(
    val selectedTab: DognalTab,
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
