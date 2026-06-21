package com.woowa.nureongi.ui.speech

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
internal actual fun rememberSpeechToTextRecognizer(): SpeechToTextRecognizer {
    return remember { IosSpeechToTextRecognizer }
}

private object IosSpeechToTextRecognizer : SpeechToTextRecognizer {
    override val isAvailable: Boolean = false

    override fun startListening(
        onResult: (List<String>) -> Unit,
        onError: (String) -> Unit,
    ) {
        onError("iOS 음성 인식은 아직 지원하지 않습니다.")
    }

    override fun stopListening() = Unit
}
