package a4.dogsignal.ui.home

import a4.dogsignal.theme.AppTheme
import a4.dogsignal.ui.component.DognalTabs
import a4.dogsignal.ui.home.composable.HomeHeader
import a4.dogsignal.ui.home.composable.HomeRecordButton
import a4.dogsignal.ui.home.composable.HomeStatusCard
import a4.dogsignal.ui.home.composable.HomeSummaryGrid
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
internal fun HomeScreen(
    state: HomeUiState = HomeUiState.preview(),
    onTabClick: (Int) -> Unit = {},
    onRecordClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HomeColors.Background)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 31.dp),
    ) {
        Spacer(Modifier.height(28.dp))
        HomeHeader(
            title = state.title,
            subtitle = state.subtitle,
        )
        Spacer(Modifier.height(28.dp))
        DognalTabs(
            tabs = state.tabs,
            selectedTabIndex = state.selectedTabIndex,
            onTabClick = onTabClick,
        )
        Spacer(Modifier.height(21.dp))
        HomeStatusCard(state.statusCard)
        Spacer(Modifier.height(22.dp))
        HomeSummaryGrid(state.summaryCards)
        Spacer(Modifier.weight(1f))
        HomeRecordButton(
            label = state.actionLabel,
            onClick = onRecordClick,
            modifier = Modifier.padding(bottom = 30.dp),
        )
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    AppTheme {
        HomeScreen()
    }
}
