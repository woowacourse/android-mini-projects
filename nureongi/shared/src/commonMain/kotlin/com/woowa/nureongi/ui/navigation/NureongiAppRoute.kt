package com.woowa.nureongi.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.woowa.nureongi.ui.screen.CurrentLocationSelectionScreen
import com.woowa.nureongi.ui.screen.DestinationSelectionScreen
import com.woowa.nureongi.ui.screen.GuidanceScreen
import com.woowa.nureongi.ui.theme.NureongiColors
import com.woowa.nureongi.ui.accessibility.rememberScreenReaderEnabled
import com.woowa.nureongi.ui.voice.rememberVoiceGuide

@Composable
internal fun NureongiAppRoute(
    modifier: Modifier = Modifier,
    viewModel: NureongiAppViewModel = viewModel { NureongiAppViewModel() },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = DestinationSelectionNavRoute,
        modifier = modifier
            .fillMaxSize()
            .background(NureongiColors.Background),
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        composable<CurrentLocationNavRoute> {
            CurrentLocationSelectionScreen(
                state = uiState.currentLocationState,
                onBackClick = navController::popBackStack,
                onLocationSelected = { locationId ->
                    viewModel.onLocationSelected(locationId)?.let {
                        navController.popBackStack<DestinationSelectionNavRoute>(
                            inclusive = false,
                        )
                    }
                },
                onVoiceLocationSelectionStarted = viewModel::onVoiceLocationSelectionStarted,
                onVoiceLocationRecognized = { recognizedTexts ->
                    viewModel.onVoiceLocationRecognized(recognizedTexts)?.let {
                        navController.popBackStack<DestinationSelectionNavRoute>(
                            inclusive = false,
                        )
                    }
                },
                onVoiceLocationRecognitionFailed = viewModel::onVoiceLocationRecognitionFailed,
            )
        }
        composable<DestinationSelectionNavRoute> {
            DestinationSelectionScreen(
                state = uiState.destinationState,
                onDestinationSelected = viewModel::onDestinationSelected,
                onChangeLocationClick = navController::navigateToCurrentLocationSelection,
                onStartGuidance = {
                    viewModel.onStartGuidance()?.let { request ->
                        navController.navigateToGuidance(request)
                    }
                },
            )
        }
        composable<GuidanceNavRoute> { backStackEntry ->
            val route: GuidanceNavRoute = backStackEntry.toRoute()
            LaunchedEffect(route) {
                viewModel.onGuidanceDestinationEntered(
                    currentLocationId = route.currentLocationId,
                    destinationId = route.destinationId,
                )
            }

            val guidanceState = uiState.guidanceState
            if (guidanceState == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(NureongiColors.Background),
                )
            } else {
                val voiceGuide = rememberVoiceGuide()
                val isScreenReaderEnabled = rememberScreenReaderEnabled()
                val guideMessage = guidanceState.currentGuidance.guideMessage

                LaunchedEffect(
                    guidanceState.currentStepIndex,
                    guidanceState.isArrived,
                ) {
                    if (!isScreenReaderEnabled) {
                        voiceGuide.speak(guideMessage)
                    }
                }

                GuidanceScreen(
                    state = guidanceState,
                    onNextStepClick = {
                        voiceGuide.stop()
                        viewModel.onNextGuidanceStep()
                    },
                    onCloseClick = {
                        voiceGuide.stop()
                        navController.finishGuidance()
                    },
                    onVoiceGuideClick = {
                        voiceGuide.speak(guideMessage)
                    },
                    onUserInteraction = voiceGuide::stop,
                )
            }
        }
    }
}
