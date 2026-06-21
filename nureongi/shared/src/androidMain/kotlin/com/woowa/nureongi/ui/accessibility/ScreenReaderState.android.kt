package com.woowa.nureongi.ui.accessibility

import android.content.Context
import android.view.accessibility.AccessibilityManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberScreenReaderEnabled(): Boolean {
    val context = LocalContext.current
    val accessibilityManager = remember {
        context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    }

    var isEnabled by remember {
        mutableStateOf(accessibilityManager.isTouchExplorationEnabled)
    }

    DisposableEffect(accessibilityManager) {
        val listener = AccessibilityManager.TouchExplorationStateChangeListener { enabled ->
            isEnabled = enabled
        }
        accessibilityManager.addTouchExplorationStateChangeListener(listener)
        isEnabled = accessibilityManager.isTouchExplorationEnabled
        onDispose {
            accessibilityManager.removeTouchExplorationStateChangeListener(listener)
        }
    }

    return isEnabled
}
