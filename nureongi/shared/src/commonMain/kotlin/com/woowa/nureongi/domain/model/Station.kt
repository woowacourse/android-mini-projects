package com.woowa.nureongi.domain.model

class Station(
    val id: String,
    val name: String,
    nodes: List<Node>,
    edges: List<Edge>,
    navigationPoints: List<NavigationPoint>,
) {
    val nodes: List<Node> = nodes.toList()
    val edges: List<Edge> = edges.toList()
    val navigationPoints: List<NavigationPoint> = navigationPoints.toList()

    init {
        require(this.nodes.map(Node::id).distinct().size == this.nodes.size) {
            "Node IDs must be unique within a station."
        }
        require(this.edges.map(Edge::id).distinct().size == this.edges.size) {
            "Edge IDs must be unique within a station."
        }
        require(this.navigationPoints.isNotEmpty()) {
            "A station must have at least one navigation point."
        }

        val nodesById = this.nodes.associateBy(Node::id)
        this.edges.forEach { edge ->
            require(nodesById[edge.from.id] == edge.from && nodesById[edge.to.id] == edge.to) {
                "Every edge endpoint must belong to the station."
            }
        }
        this.navigationPoints.forEach { navigationPoint ->
            require(navigationPoint.nodeId in nodesById) {
                "Every navigation point must reference a node in the station."
            }
        }
        require(
            this.navigationPoints.map(NavigationPoint::nodeId).distinct().size ==
                this.navigationPoints.size,
        ) {
            "Only one navigation point can reference a node."
        }

        val connectedNodeIds = this.edges
            .flatMap { edge -> listOf(edge.from.id, edge.to.id) }
            .toSet()
        require(this.nodes.all { node -> node.id in connectedNodeIds }) {
            "A station cannot contain an isolated node."
        }
        require(isStronglyConnected(nodesById.keys)) {
            "Every node in a station must be reachable from every other node."
        }
    }

    fun findNode(nodeId: String): Node? = nodes.firstOrNull { node -> node.id == nodeId }

    fun findNavigationPoint(nodeId: String): NavigationPoint? {
        return navigationPoints.firstOrNull { navigationPoint -> navigationPoint.nodeId == nodeId }
    }

    private fun isStronglyConnected(nodeIds: Set<String>): Boolean {
        val outgoingNodeIds = edges.groupBy(
            keySelector = { edge -> edge.from.id },
            valueTransform = { edge -> edge.to.id },
        )

        return nodeIds.all { startNodeId ->
            val visited = mutableSetOf(startNodeId)
            val pending = mutableListOf(startNodeId)

            while (pending.isNotEmpty()) {
                val currentNodeId = pending.removeAt(pending.lastIndex)
                outgoingNodeIds[currentNodeId].orEmpty().forEach { nextNodeId ->
                    if (visited.add(nextNodeId)) {
                        pending.add(nextNodeId)
                    }
                }
            }

            visited.containsAll(nodeIds)
        }
    }
}
