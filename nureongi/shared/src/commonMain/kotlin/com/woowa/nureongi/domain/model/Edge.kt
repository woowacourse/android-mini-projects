package com.woowa.nureongi.domain.model

data class Edge(
    val id: String,
    val from: Node,
    val to: Node,
    val distance: Float,
    val angle: Int,
    val category: EdgeCategory? = null,
) {
    init {
        require(from.id != to.id) {
            "Edge.from and Edge.to must be different nodes."
        }
        require(distance > 0f) {
            "Edge.distance must be greater than zero."
        }
        require(angle in 0..359) {
            "Edge.angle must be between 0 and 359."
        }
    }
}

enum class EdgeCategory {
    FLAT,
    STAIRS,
    ESCALATOR,
    ELEVATOR,
    FARE_GATE,
}
