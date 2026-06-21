package com.woowa.nureongi

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform