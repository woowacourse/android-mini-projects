package com.woowa.nureongi.domain.model

class Route(
    steps: List<RouteStep>,
    val totalDistance: Float,
    val startPoint: NavigationPoint,
    val destinationPoint: NavigationPoint,
) {
    val steps: List<RouteStep> = steps.toList()

    init {
        require(this.steps.isNotEmpty()) {
            "Route.steps cannot be empty."
        }
        require(this.steps.first().fromNode.id == startPoint.nodeId) {
            "The first route step must depart from the starting point."
        }
        require(this.steps.zipWithNext().all { (current, next) ->
            current.toNode == next.fromNode
        }) {
            "Route steps must be continuous."
        }
        require(this.steps.last().toNode.id == destinationPoint.nodeId) {
            "The last route step must arrive at the destination."
        }
        require(totalDistance == this.steps.sumOf { step -> step.edge.distance.toDouble() }.toFloat()) {
            "Route.totalDistance must equal the sum of its edge distances."
        }
    }

    fun isArrived(stepIndex: Int): Boolean {
        validateStepIndex(stepIndex)
        return stepIndex >= steps.size
    }

    fun currentStep(stepIndex: Int): RouteStep? {
        validateStepIndex(stepIndex)
        return steps.getOrNull(stepIndex)
    }

    fun remainingDistance(stepIndex: Int): Float {
        validateStepIndex(stepIndex)
        return steps.getOrNull(stepIndex)?.remainingDistance ?: 0f
    }

    fun advance(stepIndex: Int): Int {
        validateStepIndex(stepIndex)
        return (stepIndex + 1).coerceAtMost(steps.size)
    }

    private fun validateStepIndex(stepIndex: Int) {
        require(stepIndex in 0..steps.size) {
            "stepIndex must be between 0 and steps.size."
        }
    }
}
