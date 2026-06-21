package com.woowa.nureongi.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
internal data object CurrentLocationNavRoute

@Serializable
internal data object DestinationSelectionNavRoute

@Serializable
internal data class GuidanceNavRoute(
    val currentLocationId: String,
    val destinationId: String,
)
