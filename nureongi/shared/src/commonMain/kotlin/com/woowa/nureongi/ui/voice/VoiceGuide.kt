package com.woowa.nureongi.ui.voice

import androidx.compose.runtime.Composable

internal interface VoiceGuide {
    fun speak(message: String)

    fun stop()
}

@Composable
internal expect fun rememberVoiceGuide(): VoiceGuide
