package com.woowa.nureongi.ui.model

data class MiniMapUiModel(
    val title: String,
    val rows: Int,
    val columns: Int,
    val path: List<RouteNodeUiModel>,
)
