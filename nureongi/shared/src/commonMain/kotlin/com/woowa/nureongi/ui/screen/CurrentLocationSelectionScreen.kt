package com.woowa.nureongi.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.woowa.nureongi.ui.component.BackNavigationTopBar
import com.woowa.nureongi.ui.component.CtaButton
import com.woowa.nureongi.ui.component.PlaceListItem
import com.woowa.nureongi.ui.model.CurrentLocationItemUiModel
import com.woowa.nureongi.ui.model.CurrentLocationSelectionUiState
import com.woowa.nureongi.ui.model.PreviewCurrentLocationSelectionUiState
import com.woowa.nureongi.ui.speech.rememberSpeechToTextRecognizer
import com.woowa.nureongi.ui.theme.NureongiColors
import com.woowa.nureongi.ui.theme.NureongiTheme
import com.woowa.nureongi.ui.theme.NureongiTypography

@Composable
fun CurrentLocationSelectionScreen(
    state: CurrentLocationSelectionUiState,
    onBackClick: () -> Unit,
    onLocationSelected: (String) -> Unit,
    onVoiceLocationSelectionStarted: () -> Unit,
    onVoiceLocationRecognized: (List<String>) -> Unit,
    onVoiceLocationRecognitionFailed: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val speechToTextRecognizer = rememberSpeechToTextRecognizer()
    val voiceSelectionEnabled = !state.isLoading &&
        !state.isVoiceListening &&
        speechToTextRecognizer.isAvailable &&
        state.locations.isNotEmpty()

    DisposableEffect(speechToTextRecognizer) {
        onDispose(speechToTextRecognizer::stopListening)
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(NureongiColors.Background)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(vertical = 10.dp, horizontal = 15.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            CurrentLocationSelectionHeader(onBackClick = onBackClick)
            VoiceCurrentLocationButton(
                enabled = voiceSelectionEnabled,
                isListening = state.isVoiceListening,
                isSpeechRecognitionAvailable = speechToTextRecognizer.isAvailable,
                onClick = {
                    onVoiceLocationSelectionStarted()
                    speechToTextRecognizer.startListening(
                        onResult = onVoiceLocationRecognized,
                        onError = onVoiceLocationRecognitionFailed,
                    )
                },
            )
            state.voiceSelectionMessage?.let { message ->
                VoiceSelectionMessage(message = message)
            }
            LocationList(
                locations = state.locations,
                selectedLocationId = state.selectedLocationId,
                onLocationSelected = onLocationSelected,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun CurrentLocationSelectionHeader(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        BackNavigationTopBar(
            title = "현재 위치",
            onBackClick = onBackClick,
        )
        Text(
            text = "지금 서 있는 점형 블럭을 선택하세요.",
            style = NureongiTypography.ItemTitle,
            color = NureongiColors.TextSecondary,
        )
    }
}

@Composable
private fun VoiceCurrentLocationButton(
    enabled: Boolean,
    isListening: Boolean,
    isSpeechRecognitionAvailable: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val text = when {
        !isSpeechRecognitionAvailable -> "음성 인식 사용 불가"
        isListening -> "출발지 듣는 중"
        else -> "음성으로 출발지 선택"
    }
    CtaButton(
        text = text,
        onClick = onClick,
        enabled = enabled,
        containerColor = if (enabled) NureongiColors.Accent else NureongiColors.Disabled,
        contentColor = if (enabled) NureongiColors.OnAccent else NureongiColors.OnDisabled,
        modifier = modifier.height(56.dp),
    )
}

@Composable
private fun VoiceSelectionMessage(
    message: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = message,
        style = NureongiTypography.ItemDescription,
        color = NureongiColors.TextSecondary,
        modifier = modifier.semantics {
            liveRegion = LiveRegionMode.Polite
        },
    )
}

@Composable
private fun LocationList(
    locations: List<CurrentLocationItemUiModel>,
    selectedLocationId: String?,
    onLocationSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            items = locations,
            key = { location -> location.id },
        ) { location ->
            PlaceListItem(
                place = location.place,
                selected = location.id == selectedLocationId,
                onClick = { onLocationSelected(location.id) },
            )
        }
    }
}

@Preview
@Composable
private fun CurrentLocationSelectionScreenPreview() {
    NureongiTheme {
        CurrentLocationSelectionScreen(
            state = PreviewCurrentLocationSelectionUiState,
            onBackClick = {},
            onLocationSelected = {},
            onVoiceLocationSelectionStarted = {},
            onVoiceLocationRecognized = {},
            onVoiceLocationRecognitionFailed = {},
        )
    }
}

@Preview
@Composable
private fun UnselectedCurrentLocationSelectionScreenPreview() {
    NureongiTheme {
        CurrentLocationSelectionScreen(
            state = PreviewCurrentLocationSelectionUiState.copy(selectedLocationId = null),
            onBackClick = {},
            onLocationSelected = {},
            onVoiceLocationSelectionStarted = {},
            onVoiceLocationRecognized = {},
            onVoiceLocationRecognitionFailed = {},
        )
    }
}
