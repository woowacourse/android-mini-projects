package com.woowa.nureongi.ui.voice

import androidx.compose.ui.Modifier

internal expect fun Modifier.stopVoiceGuideOnInteraction(
    onInteraction: () -> Unit,
): Modifier
