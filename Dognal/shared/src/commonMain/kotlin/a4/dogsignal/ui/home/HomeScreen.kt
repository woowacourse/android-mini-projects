package a4.dogsignal.ui.home

import a4.dogsignal.data.repository.RecordRepository
import a4.dogsignal.model.RecordType
import a4.dogsignal.ui.common.component.DognalTab
import a4.dogsignal.ui.common.component.DognalTabs
import a4.dogsignal.ui.common.component.ScreenHeader
import a4.dogsignal.ui.home.composable.HomeStatusCard
import a4.dogsignal.ui.home.composable.HomeSummaryGrid
import a4.dogsignal.ui.theme.AppTheme
import a4.dogsignal.ui.theme.HomeBackground
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
internal fun HomeScreen(
    repository: RecordRepository,
    deviceId: String,
    onTabClick: (DognalTab) -> Unit,
) {
    val viewModel: HomeViewModel = viewModel { HomeViewModel(repository, deviceId) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.refreshTodayRecords()
    }

    HomeScreen(
        state = state,
        onTabClick = onTabClick,
    )
}

@Composable
internal fun HomeScreen(
    state: HomeUiState,
    onTabClick: (DognalTab) -> Unit = {},
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(HomeBackground)
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 31.dp),
    ) {
        Spacer(Modifier.height(28.dp))
        ScreenHeader(
            title = "도그널",
            subtitle = "오늘의 배변·패드 상태",
        )
        Spacer(Modifier.height(28.dp))
        DognalTabs(
            selectedTab = state.selectedTab,
            onTabClick = onTabClick,
            modifier = Modifier.align(Alignment.CenterHorizontally).width(250.dp),
        )
        Spacer(Modifier.height(21.dp))
        HomeStatusCard(state.statusCard)
        Spacer(Modifier.height(22.dp))
        HomeSummaryGrid(state.summaryCards)
        Spacer(Modifier.weight(1f))
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    AppTheme {
        HomeScreen(
            state =
                HomeUiState(
                    selectedTab = DognalTab.HOME,
                    statusCard =
                        HomeStatusCardState(
                            title = "마지막 배변 감지 시간",
                            description = "마지막 기록 14분 전",
                        ),
                    summaryCards =
                        listOf(
                            HomeSummaryCardState(
                                recordType = RecordType.URINE,
                                count = 4,
                            ),
                            HomeSummaryCardState(
                                recordType = RecordType.STOOL,
                                count = 1,
                            ),
                        ),
                ),
        )
    }
}
