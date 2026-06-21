package com.woowa.nureongi.domain.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class WoowaEleventhFloorMapDataTest {
    private val mapData = WoowaEleventhFloorMapData.getMapData()
    private val station = mapData.station

    @Test
    fun `11층의 노드 간선 안내 지점 데이터를 제공한다`() {
        assertEquals("woowa-eleventh-floor", station.id)
        assertEquals("우아한테크코스 11층", station.name)
        assertEquals(26, station.nodes.size)
        assertEquals(54, station.edges.size)
        assertEquals(12, station.navigationPoints.size)
    }

    @Test
    fun `일반 경유 노드는 출발지나 목적지로 노출하지 않는다`() {
        val junctionNodeId = "b"

        assertTrue(station.findNode(junctionNodeId) != null)
        assertTrue(station.navigationPoints.none { it.nodeId == junctionNodeId })
    }

    @Test
    fun `안내 지점은 출발 시 최초 방향 판단을 위한 절대각도를 가진다`() {
        val sideClassroom = station.findNavigationPoint("a")

        assertNotNull(sideClassroom)
        assertEquals(90, sideClassroom.initialAngle)
    }

    @Test
    fun `모든 노드는 같은 MapData 안에 미니맵 좌표를 가진다`() {
        assertEquals(13, mapData.rows)
        assertEquals(14, mapData.columns)
        assertEquals(station.nodes.map { it.id }.toSet(), mapData.nodePositions.keys)
    }

    @Test
    fun `Y는 C 위에 있고 Z는 G 왼쪽에 배치된다`() {
        val y = mapData.nodePositions.getValue("y")
        val c = mapData.nodePositions.getValue("c")
        val z = mapData.nodePositions.getValue("z")
        val g = mapData.nodePositions.getValue("g")

        assertTrue(y.row < c.row)
        assertEquals(y.column, c.column)
        assertEquals(z.row, g.row)
        assertTrue(z.column < g.column)
    }

    @Test
    fun `모든 경로 구간은 반대 방향 경로 구간을 가진다`() {
        station.edges.forEach { edge ->
            val reverseEdge = station.edges.firstOrNull { candidate ->
                candidate.from == edge.to && candidate.to == edge.from
            }

            assertNotNull(reverseEdge)
            assertEquals(edge.distance, reverseEdge.distance)
            assertEquals((edge.angle + 180) % 360, reverseEdge.angle)
        }
    }

    @Test
    fun `모든 노드에서 다른 모든 노드로 이동할 수 있다`() {
        station.nodes.forEach { start ->
            val reachableNodeIds = mutableSetOf(start.id)
            val pendingNodeIds = mutableListOf(start.id)

            while (pendingNodeIds.isNotEmpty()) {
                val currentNodeId = pendingNodeIds.removeAt(pendingNodeIds.lastIndex)
                station.edges
                    .filter { edge -> edge.from.id == currentNodeId }
                    .forEach { edge ->
                        if (reachableNodeIds.add(edge.to.id)) {
                            pendingNodeIds.add(edge.to.id)
                        }
                    }
            }

            assertTrue(station.nodes.all { node -> node.id in reachableNodeIds })
        }
    }
}
