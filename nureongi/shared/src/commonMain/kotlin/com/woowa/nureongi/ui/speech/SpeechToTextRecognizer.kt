package com.woowa.nureongi.ui.speech

import androidx.compose.runtime.Composable

internal interface SpeechToTextRecognizer {
    val isAvailable: Boolean

    fun startListening(
        onResult: (List<String>) -> Unit,
        onError: (String) -> Unit,
    )

    fun stopListening()
}

@Composable
internal expect fun rememberSpeechToTextRecognizer(): SpeechToTextRecognizer
