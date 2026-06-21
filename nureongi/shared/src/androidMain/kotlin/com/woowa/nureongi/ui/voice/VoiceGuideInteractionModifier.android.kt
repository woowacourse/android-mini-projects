package com.woowa.nureongi.ui.voice

import android.view.MotionEvent
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter

internal actual fun Modifier.stopVoiceGuideOnInteraction(
    onInteraction: () -> Unit,
): Modifier {
    return pointerInteropFilter { event ->
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_HOVER_ENTER,
            MotionEvent.ACTION_HOVER_MOVE,
            -> onInteraction()
        }
        false
    }
}
