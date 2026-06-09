package a4.dogsignal

import a4.dogsignal.data.network.RecordDataSource
import a4.dogsignal.data.repository.RecordRepository
import a4.dogsignal.di.createSupabase
import a4.dogsignal.ui.common.component.DognalTab
import a4.dogsignal.ui.home.HomeScreen
import a4.dogsignal.ui.record.RecordScreen
import a4.dogsignal.ui.theme.AppTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun App() {
    val supabase = remember { createSupabase() }
    val recordDataSource = remember { RecordDataSource(supabase) }

    val recordRepository = RecordRepository(recordDataSource)

    AppTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = DognalTab.HOME.name,
        ) {
            composable(DognalTab.HOME.name) {
                HomeScreen(
                    repository = recordRepository,
                    onTabClick = { tab -> navController.navigateToTab(tab) },
                    onAlertClick = {},
                )
            }
            composable(DognalTab.RECORD.name) {
                RecordScreen(
                    repository = recordRepository,
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
