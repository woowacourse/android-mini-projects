package com.woowa.nureongi.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.woowa.nureongi.ui.accessibility.rememberScreenReaderEnabled
import com.woowa.nureongi.ui.component.BrailleIcon
import com.woowa.nureongi.ui.component.CtaButton
import com.woowa.nureongi.ui.component.CurrentLocationBar
import com.woowa.nureongi.ui.component.PlaceListItem
import com.woowa.nureongi.ui.model.DestinationItemUiModel
import com.woowa.nureongi.ui.model.DestinationSelectionUiState
import com.woowa.nureongi.ui.model.PreviewDestinationSelectionUiState
import com.woowa.nureongi.ui.theme.NureongiColors
import com.woowa.nureongi.ui.theme.NureongiTheme
import com.woowa.nureongi.ui.theme.NureongiTypography
import com.woowa.nureongi.ui.voice.rememberVoiceGuide

@Composable
fun DestinationSelectionScreen(
    state: DestinationSelectionUiState,
    onDestinationSelected: (String) -> Unit,
    onChangeLocationClick: () -> Unit,
    onStartGuidance: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val voiceGuide = rememberVoiceGuide()
    val isScreenReaderEnabled = rememberScreenReaderEnabled()

    LaunchedEffect(state.error) {
        val errorMessage = state.error?.message
        if (errorMessage != null && !isScreenReaderEnabled) {
            voiceGuide.speak(errorMessage)
        }
    }

    DestinationSelectionContent(
        currentLocationName = state.currentLocationName,
        destinations = state.destinations,
        selectedDestinationId = state.selectedDestinationId,
        startGuidanceButtonText = state.startGuidanceButtonText,
        canStartGuidance = state.canStartGuidance,
        errorMessage = state.error?.message,
        onDestinationSelected = onDestinationSelected,
        onChangeLocationClick = onChangeLocationClick,
        onStartGuidance = onStartGuidance,
        modifier = modifier,
    )
}

@Composable
private fun DestinationSelectionContent(
    currentLocationName: String,
    destinations: List<DestinationItemUiModel>,
    selectedDestinationId: String?,
    startGuidanceButtonText: String,
    canStartGuidance: Boolean,
    errorMessage: String?,
    onDestinationSelected: (String) -> Unit,
    onChangeLocationClick: () -> Unit,
    onStartGuidance: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = NureongiColors.Background,
            ) {
                DestinationSelectionCtaButton(
                    modifier = Modifier
                        .height(72.dp),
                    text = startGuidanceButtonText,
                    enabled = canStartGuidance,
                    onStartGuidance = onStartGuidance,
                )
            }
        },
        modifier = modifier
            .fillMaxSize()
            .background(NureongiColors.Background),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(vertical = 10.dp, horizontal = 15.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            DestinationSelectionHeader()
            Text(
                text = "현재 위치를 선택해주세요.",
                style = NureongiTypography.SectionHeader,
                color = NureongiColors.TextPrimary,
                modifier = Modifier.semantics { heading() }
            )
            CurrentLocationBar(
                locationName = currentLocationName,
                onChangeClick = onChangeLocationClick,
            )
            errorMessage?.let { message ->
                DestinationSelectionErrorMessage(message = message)
            }
            DestinationOptions(
                modifier = Modifier.weight(1f),
                destinations = destinations,
                selectedDestinationId = selectedDestinationId,
                onDestinationSelected = onDestinationSelected,
            )
        }
    }
}

@Composable
private fun DestinationSelectionErrorMessage(
    modifier: Modifier = Modifier,
    message: String,
) {
    Text(
        text = message,
        style = NureongiTypography.ItemDescription,
        color = NureongiColors.Accent,
        modifier = modifier.semantics {
            liveRegion = LiveRegionMode.Assertive
        },
    )
}

@Composable
private fun DestinationSelectionHeader(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BrailleIcon(
            dotColor = NureongiColors.Accent,
            backgroundColor = NureongiColors.Background,
            modifier = Modifier.size(65.dp)
        )
        Column {
            Text(
                text = "누렁이",
                style = NureongiTypography.SectionHeader,
                color = NureongiColors.TextPrimary,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                text = "점자 블록 길안내",
                style = NureongiTypography.ItemDescription,
                color = NureongiColors.Accent,
            )
        }
    }
}

@Composable
private fun DestinationOptions(
    destinations: List<DestinationItemUiModel>,
    selectedDestinationId: String?,
    onDestinationSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "어디로 갈까요?",
                style = NureongiTypography.SectionHeader,
                color = NureongiColors.TextPrimary,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                text = "목적지를 선택하세요.",
                style = NureongiTypography.ItemDescription,
                color = NureongiColors.TextSecondary,
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(
                items = destinations,
                key = { destination -> destination.id },
            ) { destination ->
                PlaceListItem(
                    place = destination.place,
                    selected = destination.id == selectedDestinationId,
                    onClick = { onDestinationSelected(destination.id) },
                )
            }
        }
    }
}

@Composable
private fun DestinationSelectionCtaButton(
    text: String,
    enabled: Boolean,
    onStartGuidance: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CtaButton(
        text = text,
        onClick = onStartGuidance,
        enabled = enabled,
        containerColor = if (enabled) NureongiColors.Accent else NureongiColors.Disabled,
        contentColor = if (enabled) NureongiColors.OnAccent else NureongiColors.OnDisabled,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun DestinationSelectionScreenPreview() {
    NureongiTheme {
        DestinationSelectionScreen(
            state = PreviewDestinationSelectionUiState.copy(selectedDestinationId = "e"),
            onDestinationSelected = {},
            onChangeLocationClick = {},
            onStartGuidance = {},
        )
    }
}

@Preview
@Composable
private fun UnselectedDestinationSelectionScreenPreview() {
    NureongiTheme {
        DestinationSelectionScreen(
            state = PreviewDestinationSelectionUiState,
            onDestinationSelected = {},
            onChangeLocationClick = {},
            onStartGuidance = {},
        )
    }
}
