package com.woowa.nureongi.domain.service

import com.woowa.nureongi.domain.model.Edge
import com.woowa.nureongi.domain.model.NavigationPoint
import com.woowa.nureongi.domain.model.Node
import com.woowa.nureongi.domain.model.Route
import com.woowa.nureongi.domain.model.RouteStep
import com.woowa.nureongi.domain.model.Station

class Navigator : Navigatable {
    override fun findRoute(
        station: Station,
        from: NavigationPoint,
        destination: NavigationPoint,
    ): Route {
        require(from in station.navigationPoints) {
            "The start point must belong to the station."
        }
        val start = requireNotNull(station.findNode(from.nodeId))
        val destinationNode = requireNotNull(station.findNode(destination.nodeId)) {
            "The destination must belong to the station."
        }
        require(destination in station.navigationPoints) {
            "The destination point must belong to the station."
        }
        require(start != destinationNode) {
            "The start node and destination node must be different."
        }

        val distances = station.nodes.associate { node ->
            node.id to if (node == start) 0f else Float.POSITIVE_INFINITY
        }.toMutableMap()
        val previousEdges = mutableMapOf<String, Edge>()
        val unvisitedNodeIds = station.nodes.map(Node::id).toMutableSet()

        while (unvisitedNodeIds.isNotEmpty()) {
            val currentNodeId = unvisitedNodeIds.minByOrNull { nodeId ->
                distances.getValue(nodeId)
            } ?: break
            if (currentNodeId == destinationNode.id) {
                break
            }
            unvisitedNodeIds.remove(currentNodeId)

            station.edges
                .filter { edge -> edge.from.id == currentNodeId && edge.to.id in unvisitedNodeIds }
                .forEach { edge ->
                    val candidateDistance = distances.getValue(currentNodeId) + edge.distance
                    if (candidateDistance < distances.getValue(edge.to.id)) {
                        distances[edge.to.id] = candidateDistance
                        previousEdges[edge.to.id] = edge
                    }
                }
        }

        val routeEdges = buildList {
            var currentNode = destinationNode
            while (currentNode != start) {
                val edge = checkNotNull(previousEdges[currentNode.id]) {
                    "The station graph must contain a route to the destination."
                }
                add(edge)
                currentNode = edge.from
            }
        }.asReversed()

        var remainingDistance = routeEdges.sumOf { edge -> edge.distance.toDouble() }.toFloat()
        val steps = routeEdges.map { edge ->
            RouteStep(
                fromNode = edge.from,
                toNode = edge.to,
                edge = edge,
                remainingDistance = remainingDistance,
            ).also {
                remainingDistance -= edge.distance
            }
        }

        return Route(
            steps = steps,
            totalDistance = routeEdges.sumOf { edge -> edge.distance.toDouble() }.toFloat(),
            startPoint = from,
            destinationPoint = destination,
        )
    }
}
