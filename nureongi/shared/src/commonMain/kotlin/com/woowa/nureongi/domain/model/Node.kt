package com.woowa.nureongi.domain.model

data class Node(
    val id: String,
    val name: String,
    val landmark: String? = null,
    val floor: Int,
)
