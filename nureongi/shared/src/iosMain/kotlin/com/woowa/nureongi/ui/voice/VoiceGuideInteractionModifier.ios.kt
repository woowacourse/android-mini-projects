package com.woowa.nureongi.ui.voice

import androidx.compose.ui.Modifier

internal actual fun Modifier.stopVoiceGuideOnInteraction(
    onInteraction: () -> Unit,
): Modifier = this
