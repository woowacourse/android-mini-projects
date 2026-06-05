package a4.dogsignal.ui.home

import a4.dogsignal.ui.component.DognalTabState

internal data class HomeUiState(
    val title: String,
    val subtitle: String,
    val tabs: List<DognalTabState>,
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
    val label: String,
    val value: String,
    val tone: HomeSummaryTone,
)

internal enum class HomeSummaryTone {
    Warm,
    Cool,
}
