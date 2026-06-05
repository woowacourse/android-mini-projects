package a4.dogsignal.home

internal data class HomeUiState(
    val title: String,
    val subtitle: String,
    val tabs: List<HomeTabState>,
    val selectedTabIndex: Int,
    val statusCard: HomeStatusCardState,
    val summaryCards: List<HomeSummaryCardState>,
    val actionLabel: String,
) {
    companion object {
        fun preview(): HomeUiState = HomeUiState(
            title = "도그널",
            subtitle = "오늘의 배변·패드 상태",
            tabs = listOf(
                HomeTabState("홈"),
                HomeTabState("기록"),
                HomeTabState("설정"),
            ),
            selectedTabIndex = 0,
            statusCard = HomeStatusCardState(
                title = "마지막 배변 감지 시간",
                description = "마지막 기록 14분 전"
            ),
            summaryCards = listOf(
                HomeSummaryCardState(
                    label = "소변",
                    value = "4회",
                    tone = HomeSummaryTone.Warm,
                ),
                HomeSummaryCardState(
                    label = "대변",
                    value = "1회",
                    tone = HomeSummaryTone.Cool,
                ),
            ),
            actionLabel = "감지 시 LED 점등",
        )
    }
}

internal data class HomeTabState(
    val label: String,
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
