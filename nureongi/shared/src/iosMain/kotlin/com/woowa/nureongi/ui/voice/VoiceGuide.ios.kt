package com.woowa.nureongi.ui.voice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import platform.AVFAudio.AVSpeechBoundary
import platform.AVFAudio.AVSpeechSynthesisVoice
import platform.AVFAudio.AVSpeechSynthesizer
import platform.AVFAudio.AVSpeechUtterance
import platform.AVFAudio.AVSpeechUtteranceDefaultSpeechRate

@Composable
internal actual fun rememberVoiceGuide(): VoiceGuide {
    val voiceGuide = remember {
        IosVoiceGuide()
    }

    DisposableEffect(voiceGuide) {
        onDispose(voiceGuide::stop)
    }

    return voiceGuide
}

private class IosVoiceGuide : VoiceGuide {
    private val synthesizer = AVSpeechSynthesizer()

    override fun speak(message: String) {
        if (message.isBlank()) {
            return
        }

        synthesizer.stopSpeakingAtBoundary(AVSpeechBoundary.AVSpeechBoundaryImmediate)
        val utterance = AVSpeechUtterance(string = message).apply {
            voice = AVSpeechSynthesisVoice.voiceWithLanguage(KOREAN_LANGUAGE_CODE)
            rate = AVSpeechUtteranceDefaultSpeechRate
        }
        synthesizer.speakUtterance(utterance)
    }

    override fun stop() {
        synthesizer.stopSpeakingAtBoundary(AVSpeechBoundary.AVSpeechBoundaryImmediate)
    }
}

private const val KOREAN_LANGUAGE_CODE = "ko-KR"
