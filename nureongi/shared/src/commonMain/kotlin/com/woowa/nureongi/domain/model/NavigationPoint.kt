package com.woowa.nureongi.domain.model

data class NavigationPoint(
    val nodeId: String,
    val initialAngle: Int,
) {
    init {
        require(initialAngle in 0..359) {
            "NavigationPoint.initialAngle must be between 0 and 359."
        }
    }
}
