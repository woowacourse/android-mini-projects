package com.woowa.nureongi.ui.speech

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import java.util.Locale

@Composable
internal actual fun rememberSpeechToTextRecognizer(): SpeechToTextRecognizer {
    val context = LocalContext.current.applicationContext
    val recognizerHolder = remember { arrayOfNulls<AndroidSpeechToTextRecognizer>(1) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        recognizerHolder[0]?.onPermissionResult(isGranted)
    }

    val currentRecognizer = remember(context) {
        AndroidSpeechToTextRecognizer(
            context = context,
            requestRecordAudioPermission = {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            },
        )
    }
    recognizerHolder[0] = currentRecognizer

    DisposableEffect(currentRecognizer) {
        onDispose(currentRecognizer::release)
    }

    return currentRecognizer
}

private class AndroidSpeechToTextRecognizer(
    private val context: Context,
    private val requestRecordAudioPermission: () -> Unit,
) : SpeechToTextRecognizer {
    override val isAvailable: Boolean
        get() = SpeechRecognizer.isRecognitionAvailable(context)

    private var speechRecognizer: SpeechRecognizer? = null
    private var pendingResult: ((List<String>) -> Unit)? = null
    private var pendingError: ((String) -> Unit)? = null

    override fun startListening(
        onResult: (List<String>) -> Unit,
        onError: (String) -> Unit,
    ) {
        if (!isAvailable) {
            onError("음성 인식을 사용할 수 없습니다.")
            return
        }

        pendingResult = onResult
        pendingError = onError

        if (!hasRecordAudioPermission()) {
            requestRecordAudioPermission()
            return
        }

        startRecognition()
    }

    override fun stopListening() {
        speechRecognizer?.stopListening()
    }

    fun onPermissionResult(isGranted: Boolean) {
        if (isGranted) {
            startRecognition()
        } else {
            pendingError?.invoke("마이크 권한이 필요합니다.")
            clearCallbacks()
        }
    }

    fun release() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        clearCallbacks()
    }

    private fun startRecognition() {
        val recognizer = speechRecognizer ?: SpeechRecognizer.createSpeechRecognizer(context)
            .also { speechRecognizer = it }
        recognizer.setRecognitionListener(createRecognitionListener())
        recognizer.startListening(createRecognizerIntent())
    }

    private fun createRecognitionListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) = Unit

            override fun onBeginningOfSpeech() = Unit

            override fun onRmsChanged(rmsdB: Float) = Unit

            override fun onBufferReceived(buffer: ByteArray?) = Unit

            override fun onEndOfSpeech() = Unit

            override fun onError(error: Int) {
                pendingError?.invoke(error.toSpeechRecognitionMessage())
                clearCallbacks()
            }

            override fun onResults(results: Bundle?) {
                val candidates = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    .orEmpty()
                if (candidates.isEmpty()) {
                    pendingError?.invoke("출발지를 듣지 못했습니다. 다시 말씀해주세요.")
                } else {
                    pendingResult?.invoke(candidates)
                }
                clearCallbacks()
            }

            override fun onPartialResults(partialResults: Bundle?) = Unit

            override fun onEvent(
                eventType: Int,
                params: Bundle?,
            ) = Unit
        }
    }

    private fun createRecognizerIntent(): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREA.toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        }
    }

    private fun hasRecordAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO,
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun clearCallbacks() {
        pendingResult = null
        pendingError = null
    }
}

private fun Int.toSpeechRecognitionMessage(): String {
    return when (this) {
        SpeechRecognizer.ERROR_AUDIO -> "마이크 입력 중 오류가 발생했습니다."
        SpeechRecognizer.ERROR_CLIENT -> "음성 인식을 시작하지 못했습니다."
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "마이크 권한이 필요합니다."
        SpeechRecognizer.ERROR_NETWORK,
        SpeechRecognizer.ERROR_NETWORK_TIMEOUT,
        SpeechRecognizer.ERROR_SERVER,
        -> "네트워크 상태를 확인한 뒤 다시 시도해주세요."

        SpeechRecognizer.ERROR_NO_MATCH,
        SpeechRecognizer.ERROR_SPEECH_TIMEOUT,
        -> "출발지를 듣지 못했습니다. 다시 말씀해주세요."

        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "음성 인식이 이미 실행 중입니다."
        else -> "음성 인식에 실패했습니다. 다시 시도해주세요."
    }
}
