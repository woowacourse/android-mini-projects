package com.woowa.nureongi.ui.navigation

import androidx.navigation.NavController

internal fun NavController.navigateToCurrentLocationSelection() {
    navigate(CurrentLocationNavRoute) {
        launchSingleTop = true
    }
}

internal fun NavController.navigateToGuidance(route: GuidanceNavRoute) {
    navigate(route) {
        launchSingleTop = true
    }
}

internal fun NavController.finishGuidance(): Boolean {
    return popBackStack<DestinationSelectionNavRoute>(inclusive = false)
}
