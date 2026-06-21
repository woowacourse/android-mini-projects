package com.woowa.nureongi.domain.service

import com.woowa.nureongi.domain.model.NavigationPoint
import com.woowa.nureongi.domain.model.Route
import com.woowa.nureongi.domain.model.Station

fun interface Navigatable {
    fun findRoute(
        station: Station,
        from: NavigationPoint,
        destination: NavigationPoint,
    ): Route
}
