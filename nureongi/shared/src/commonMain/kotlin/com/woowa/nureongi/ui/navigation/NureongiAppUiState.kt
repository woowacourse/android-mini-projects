package com.woowa.nureongi.ui.navigation

import com.woowa.nureongi.domain.data.StationMapData
import com.woowa.nureongi.ui.model.CurrentLocationSelectionUiState
import com.woowa.nureongi.ui.model.DestinationSelectionUiState
import com.woowa.nureongi.ui.model.GuidanceUiState
import com.woowa.nureongi.ui.model.toCurrentLocationItems
import com.woowa.nureongi.ui.model.toDestinationItems

internal data class NureongiAppUiState(
    val currentLocationState: CurrentLocationSelectionUiState = CurrentLocationSelectionUiState(),
    val destinationState: DestinationSelectionUiState = DestinationSelectionUiState(),
    val guidanceState: GuidanceUiState? = null,
)

internal fun StationMapData.toInitialAppUiState(): NureongiAppUiState {
    return NureongiAppUiState(
        currentLocationState = CurrentLocationSelectionUiState(
            locations = toCurrentLocationItems(),
        ),
        destinationState = DestinationSelectionUiState(
            destinations = toDestinationItems(),
        ),
    )
}
