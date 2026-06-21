package com.woowa.nureongi.ui.model

data class RouteNodeUiModel(
    val row: Int,
    val column: Int,
    val label: String? = null,
    val state: State = State.NEUTRAL,
) {
    enum class State {
        NEUTRAL,
        PASSED,
        HIGHLIGHTED,
    }
}
