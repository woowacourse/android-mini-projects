package com.woowa.nureongi.ui.model

data class CurrentLocationSelectionUiState(
    val locations: List<CurrentLocationItemUiModel> = emptyList(),
    val selectedLocationId: String? = null,
    val isLoading: Boolean = false,
    val isVoiceListening: Boolean = false,
    val voiceSelectionMessage: String? = null,
    val error: UiError? = null,
) {
    val selectedLocation: CurrentLocationItemUiModel?
        get() = locations.firstOrNull { it.id == selectedLocationId }

    val selectedLocationIdForResult: String?
        get() = if (isLoading) null else selectedLocation?.id
}

data class CurrentLocationItemUiModel(
    val id: String,
    val place: PlaceUiModel,
)

internal val PreviewCurrentLocationSelectionUiState = CurrentLocationSelectionUiState(
    locations = PreviewStationMapData.toCurrentLocationItems(),
    selectedLocationId = "a",
)
