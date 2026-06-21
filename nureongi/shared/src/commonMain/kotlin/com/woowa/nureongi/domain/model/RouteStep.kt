package com.woowa.nureongi.domain.model

data class RouteStep(
    val fromNode: Node,
    val toNode: Node,
    val edge: Edge,
    val remainingDistance: Float,
) {
    init {
        require(edge.from == fromNode && edge.to == toNode) {
            "RouteStep nodes must match the edge endpoints."
        }
        require(remainingDistance >= 0f) {
            "RouteStep.remainingDistance cannot be negative."
        }
    }
}
