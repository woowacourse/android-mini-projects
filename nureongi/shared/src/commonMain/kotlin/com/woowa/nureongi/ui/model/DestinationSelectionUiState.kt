package com.woowa.nureongi.ui.model

data class DestinationSelectionUiState(
    val currentLocation: CurrentLocationUiModel? = null,
    val destinations: List<DestinationItemUiModel> = emptyList(),
    val selectedDestinationId: String? = null,
    val isLoading: Boolean = false,
    val error: UiError? = null,
) {
    val currentLocationName: String
        get() = currentLocation?.name ?: "현재 위치를 선택해 주세요"

    val selectedDestination: DestinationItemUiModel?
        get() = destinations.firstOrNull { it.id == selectedDestinationId }

    val startGuidanceDestinationId: String?
        get() = if (!isLoading && currentLocation != null && currentLocation.nodeId != selectedDestination?.id) {
            selectedDestination?.id
        } else {
            null
        }

    val canStartGuidance: Boolean
        get() = startGuidanceDestinationId != null

    val startGuidanceButtonText: String
        get() = selectedDestination?.let { "${it.place.name}까지 안내 시작" }
            ?: "목적지를 선택하세요"
}

data class CurrentLocationUiModel(
    val nodeId: String,
    val name: String,
)

data class DestinationItemUiModel(
    val id: String,
    val place: PlaceUiModel,
)

internal val PreviewDestinationSelectionUiState = DestinationSelectionUiState(
    currentLocation = CurrentLocationUiModel(
        nodeId = "a",
        name = "옆 강의실",
    ),
    destinations = PreviewStationMapData.toDestinationItems(),
)
