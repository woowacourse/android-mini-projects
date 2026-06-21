package com.woowa.nureongi.ui.model

import com.woowa.nureongi.domain.data.StationMapData
import com.woowa.nureongi.domain.data.SeohyeonBasementStationMapData

internal fun StationMapData.toCurrentLocationItems(): List<CurrentLocationItemUiModel> {
    return station.navigationPoints.map { navigationPoint ->
        val node = requireNotNull(station.findNode(navigationPoint.nodeId))
        CurrentLocationItemUiModel(
            id = navigationPoint.nodeId,
            place = PlaceUiModel(
                name = node.name,
                location = node.landmark.orEmpty(),
            ),
        )
    }
}

internal fun StationMapData.toDestinationItems(): List<DestinationItemUiModel> {
    return station.navigationPoints.map { navigationPoint ->
        val node = requireNotNull(station.findNode(navigationPoint.nodeId))
        DestinationItemUiModel(
            id = navigationPoint.nodeId,
            place = PlaceUiModel(
                name = node.name,
                location = node.landmark.orEmpty(),
            ),
        )
    }
}

internal val PreviewStationMapData = SeohyeonBasementStationMapData.getMapData()
