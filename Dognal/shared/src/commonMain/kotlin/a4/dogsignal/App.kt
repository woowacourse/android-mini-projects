package a4.dogsignal

import a4.dogsignal.data.local.DeviceLocalDataSource
import a4.dogsignal.data.network.DeviceDataSource
import a4.dogsignal.data.network.RecordDataSource
import a4.dogsignal.data.repository.DeviceRepository
import a4.dogsignal.data.repository.RecordRepository
import a4.dogsignal.di.createSupabase
import a4.dogsignal.ui.common.component.DognalTab
import a4.dogsignal.ui.device.DeviceInfoScreen
import a4.dogsignal.ui.home.HomeScreen
import a4.dogsignal.ui.record.RecordRoute
import a4.dogsignal.ui.record.RecordViewModel
import a4.dogsignal.ui.theme.AppTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

private object Route {
    const val DEVICE_INFO = "device_info"
}

@Composable
fun App() {
    val supabase = remember { createSupabase() }
    val deviceLocalDataSource = remember { DeviceLocalDataSource() }
    val deviceDataSource = remember { DeviceDataSource(supabase) }
    val deviceRepository = remember { DeviceRepository(deviceLocalDataSource, deviceDataSource) }
    val recordDataSource = remember { RecordDataSource(supabase) }
    val recordRepository = remember { RecordRepository(recordDataSource) }

    var deviceId by remember { mutableStateOf<String?>(null) }
    var isReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        deviceId = deviceRepository.getDeviceId()
        isReady = true
    }

    AppTheme {
        if (!isReady) return@AppTheme

        val navController = rememberNavController()
        val startDestination = if (deviceId != null) DognalTab.HOME.name else Route.DEVICE_INFO

        NavHost(
            navController = navController,
            startDestination = startDestination,
        ) {
            composable(Route.DEVICE_INFO) {
                DeviceInfoScreen(
                    deviceRepository = deviceRepository,
                    onAuthenticated = { id ->
                        deviceId = id
                        navController.navigate(DognalTab.HOME.name) {
                            popUpTo(Route.DEVICE_INFO) { inclusive = true }
                        }
                    },
                )
            }
            composable(DognalTab.HOME.name) {
                val currentDeviceId = deviceId ?: return@composable
                HomeScreen(
                    repository = recordRepository,
                    deviceId = currentDeviceId,
                    onTabClick = { tab -> navController.navigateToTab(tab) },
                )
            }
            composable(DognalTab.RECORD.name) {
                val currentDeviceId = deviceId ?: return@composable
                val recordViewModel: RecordViewModel =
                    viewModel {
                        RecordViewModel(recordRepository, currentDeviceId)
                    }

                RecordRoute(
                    viewModel = recordViewModel,
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
