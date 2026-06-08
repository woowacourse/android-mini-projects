package a4.dogsignal

import a4.dogsignal.ui.common.component.DognalTab
import a4.dogsignal.ui.home.HomeStatusCardState
import a4.dogsignal.ui.home.HomeScreen
import a4.dogsignal.ui.home.HomeUiState
import a4.dogsignal.ui.record.RecordScreen
import a4.dogsignal.ui.record.RecordUiState
import a4.dogsignal.ui.theme.AppTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun App() {
    AppTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = DognalTab.HOME.name,
        ) {
            composable(DognalTab.HOME.name) {
                HomeScreen(
                    state = HomeUiState(
                        selectedTab = DognalTab.HOME,
                        statusCard = HomeStatusCardState("", ""),
                        summaryCards = emptyList(),
                    ),
                    onTabClick = { tab -> navController.navigateToTab(tab) },
                    onRecordClick = { navController.navigateToTab(DognalTab.RECORD) },
                )
            }
            composable(DognalTab.RECORD.name) {
                RecordScreen(
                    state = RecordUiState(
                        selectedTab = DognalTab.RECORD,
                        dateLabel = "오늘",
                        dateValue = "",
                        recordList = emptyList(),
                    ),
                    onAddRecordClick = {},
                    onTabClick = { tab -> navController.navigateToTab(tab) },
                )
            }
        }
    }
}

private fun NavController.navigateToTab(tab: DognalTab) {
    navigate(tab.name) {
        popUpTo(DognalTab.HOME.name) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
