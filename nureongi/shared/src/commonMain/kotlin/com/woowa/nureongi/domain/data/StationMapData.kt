package com.woowa.nureongi.domain.data

import com.woowa.nureongi.domain.model.Station

data class StationMapData(
    val station: Station,
    val rows: Int,
    val columns: Int,
    val nodePositions: Map<String, MapNodePosition>,
) {
    init {
        require(rows > 0 && columns > 0) {
            "Map rows and columns must be greater than zero."
        }

        val nodeIds = station.nodes.map { node -> node.id }.toSet()
        require(nodePositions.keys == nodeIds) {
            "Every station node must have exactly one map position."
        }
        require(nodePositions.values.all { position ->
            position.row in 0 until rows && position.column in 0 until columns
        }) {
            "Every node position must be within the map bounds."
        }
    }
}

data class MapNodePosition(
    val row: Int,
    val column: Int,
)

fun interface StationMapDataSource {
    fun getMapData(): StationMapData
}
