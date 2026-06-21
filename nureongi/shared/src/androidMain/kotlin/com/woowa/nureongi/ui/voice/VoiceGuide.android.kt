package com.woowa.nureongi.ui.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Composable
internal actual fun rememberVoiceGuide(): VoiceGuide {
    val context = LocalContext.current.applicationContext
    val voiceGuide = remember {
        AndroidVoiceGuide(context)
    }

    DisposableEffect(Unit) {
        onDispose(voiceGuide::release)
    }

    return voiceGuide
}

private class AndroidVoiceGuide(
    context: Context,
) : VoiceGuide, TextToSpeech.OnInitListener {
    private var textToSpeech: TextToSpeech? = null
    private var isReady = false
    private var pendingMessage: String? = null

    init {
        textToSpeech = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        val textToSpeech = textToSpeech ?: return
        if (status != TextToSpeech.SUCCESS) {
            pendingMessage = null
            return
        }

        val languageResult = textToSpeech.setLanguage(Locale.KOREAN)
        isReady = languageResult != TextToSpeech.LANG_MISSING_DATA &&
            languageResult != TextToSpeech.LANG_NOT_SUPPORTED

        if (isReady) {
            pendingMessage?.let(::speakNow)
        }
        pendingMessage = null
    }

    override fun speak(message: String) {
        if (message.isBlank()) {
            return
        }

        if (isReady) {
            speakNow(message)
        } else {
            pendingMessage = message
        }
    }

    override fun stop() {
        pendingMessage = null
        textToSpeech?.stop()
    }

    fun release() {
        stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        isReady = false
    }

    private fun speakNow(message: String) {
        textToSpeech?.speak(
            message,
            TextToSpeech.QUEUE_FLUSH,
            null,
            VOICE_GUIDE_UTTERANCE_ID,
        )
    }
}

private const val VOICE_GUIDE_UTTERANCE_ID = "nureongi_voice_guide"
