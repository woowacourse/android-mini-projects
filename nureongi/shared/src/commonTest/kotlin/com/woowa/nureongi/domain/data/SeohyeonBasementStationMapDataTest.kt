package com.woowa.nureongi.domain.data

import com.woowa.nureongi.domain.model.EdgeCategory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SeohyeonBasementStationMapDataTest {
    private val mapData = SeohyeonBasementStationMapData.getMapData()
    private val station = mapData.station

    @Test
    fun `provides seohyeon basement and platform map data`() {
        assertEquals("seohyeon-basement", station.id)
        assertEquals("서현역 지하철역", station.name)
        assertEquals(70, station.nodes.size)
        assertEquals(19, station.navigationPoints.size)
    }

    @Test
    fun `connects confirmed basement first floor paths`() {
        assertEdge("exit-3", "n1", 12f)
        assertEdge("n1", "n2", 20f)
        assertEdge("n2", "restroom-women", 14f)
        assertEdge("n3", "n4", 45f)
        assertEdge("n4", "n6", 31f)
        assertEdge("n7", "gate-upper-outside", 4f)
        assertEdge("n13", "gate-lower-outside", 3f)
        assertEdge("n24", "stairs-lower-suwon-a", 7f)
        assertEdge("n30", "stairs-lower-wangsimni-b", 3f)
    }

    @Test
    fun `connects fare gates and vertical movements with categories`() {
        assertEdge("gate-upper-outside", "gate-upper-inside", 1f, EdgeCategory.FARE_GATE)
        assertEdge("gate-lower-outside", "gate-lower-inside", 1f, EdgeCategory.FARE_GATE)
        assertEdge("stairs-upper-suwon", "b2-upper-suwon-entry", 1f, EdgeCategory.STAIRS)
        assertEdge("elevator-lower-suwon", "b2-lower-suwon-elevator-entry", 1f, EdgeCategory.ELEVATOR)
        assertEdge("elevator-lower-wangsimni", "b2-lower-wangsimni-elevator-entry", 1f, EdgeCategory.ELEVATOR)
    }

    @Test
    fun `connects subway platform destinations`() {
        assertEdge("b2-upper-suwon-n1", "platform-suwon-2-2", 15f)
        assertEdge("stairs-upper-wangsimni", "platform-wangsimni-7-4-upper", 1f, EdgeCategory.STAIRS)
        assertEdge("b2-lower-suwon-a-n1", "platform-suwon-5-4", 15f)
        assertEdge("b2-lower-suwon-b-n1", "platform-suwon-7-4", 17f)
        assertEdge("b2-lower-suwon-elevator-entry", "platform-suwon-6-4", 10f)
        assertEdge("b2-lower-wangsimni-a-n1", "platform-wangsimni-4-1", 17f)
        assertEdge("b2-lower-wangsimni-b-n1", "platform-wangsimni-2-2", 17f)
        assertEdge("b2-lower-wangsimni-elevator-n2", "platform-wangsimni-3-1", 2f)
    }

    @Test
    fun `does not expose stairs and elevators as navigation points`() {
        val navigationPointIds = station.navigationPoints.map { it.nodeId }.toSet()

        assertTrue("platform-suwon-2-2" in navigationPointIds)
        assertTrue("platform-wangsimni-3-1" in navigationPointIds)
        assertNull(station.findNavigationPoint("stairs-lower-suwon-a"))
        assertNull(station.findNavigationPoint("stairs-lower-wangsimni-b"))
        assertNull(station.findNavigationPoint("elevator-lower-suwon"))
        assertNull(station.findNavigationPoint("elevator-lower-wangsimni"))
    }

    @Test
    fun `all edges have reverse edges`() {
        station.edges.forEach { edge ->
            val reverseEdge = station.edges.firstOrNull { candidate ->
                candidate.from == edge.to && candidate.to == edge.from
            }

            assertNotNull(reverseEdge)
            assertEquals(edge.distance, reverseEdge.distance)
            assertEquals((edge.angle + 180) % 360, reverseEdge.angle)
            assertEquals(edge.category, reverseEdge.category)
        }
    }

    private fun assertEdge(
        fromNodeId: String,
        toNodeId: String,
        distance: Float,
        category: EdgeCategory? = null,
    ) {
        val edge = station.edges.firstOrNull { edge ->
            edge.from.id == fromNodeId && edge.to.id == toNodeId
        }

        assertNotNull(edge)
        assertEquals(distance, edge.distance)
        assertEquals(category, edge.category)
    }
}
