package com.woowa.nureongi.domain.data

import com.woowa.nureongi.domain.model.Edge
import com.woowa.nureongi.domain.model.EdgeCategory
import com.woowa.nureongi.domain.model.NavigationPoint
import com.woowa.nureongi.domain.model.Node
import com.woowa.nureongi.domain.model.Station

object SeohyeonBasementStationMapData : StationMapDataSource {
    private val data = createMapData()

    override fun getMapData(): StationMapData = data

    private fun createMapData(): StationMapData {
        val nodesById = listOf(
            node("exit-3", "3번 출구", floor = -1),
            node("n1", "N1 점형 블록", floor = -1),
            node("n2", "N2 점형 블록", floor = -1),
            node("restroom-women", "여자 화장실", floor = -1),
            node("n3", "N3 점형 블록", floor = -1),
            node("n4", "N4 점형 블록", floor = -1),
            node("n5", "N5 점형 블록", floor = -1),
            node("n6", "N6 점형 블록", floor = -1),
            node("ticket-office", "매표소", floor = -1),
            node("n7", "N7 점형 블록", floor = -1),
            node("n8", "N8 점형 블록", floor = -1),
            node("gate-upper-outside", "상단 개찰구 바깥쪽", floor = -1),
            node("gate-upper-inside", "상단 개찰구 안쪽", floor = -1),
            node("exit-4", "4번 출구", floor = -1),
            node("n9", "N9 점형 블록", floor = -1),
            node("n10", "N10 점형 블록", floor = -1),
            node("exit-5", "5번 출구", floor = -1),
            node("n11", "N11 점형 블록", floor = -1),
            node("n12", "N12 점형 블록", floor = -1),
            node("restroom-men", "남자 화장실", floor = -1),
            node("exit-1", "1번 출구", floor = -1),
            node("n13", "N13 점형 블록", floor = -1),
            node("gate-lower-outside", "하단 개찰구 바깥쪽", floor = -1),
            node("gate-lower-inside", "하단 개찰구 안쪽", floor = -1),
            node("n15", "N15 점형 블록", floor = -1),
            node("n16", "N16 점형 블록", floor = -1),
            node("stairs-upper-suwon", "수원역 방면 지하철 계단", floor = -1),
            node("n17", "N17 점형 블록", floor = -1),
            node("stairs-upper-wangsimni", "왕십리역 방면 지하철 계단", floor = -1),
            node("n18", "N18 점형 블록", floor = -1),
            node("n19", "N19 점형 블록", floor = -1),
            node("n20", "N20 점형 블록", floor = -1),
            node("n21", "N21 점형 블록", floor = -1),
            node("elevator-lower-suwon", "수원역 방면 승강장 엘리베이터", floor = -1),
            node("n22", "N22 점형 블록", floor = -1),
            node("n23", "N23 점형 블록", floor = -1),
            node("n24", "N24 점형 블록", floor = -1),
            node("stairs-lower-suwon-a", "수원역 방면 지하철 계단 A", floor = -1),
            node("n25", "N25 점형 블록", floor = -1),
            node("stairs-lower-suwon-b", "수원역 방면 지하철 계단 B", floor = -1),
            node("elevator-lower-wangsimni", "왕십리역 방면 승강장 엘리베이터", floor = -1),
            node("n26", "N26 점형 블록", floor = -1),
            node("n27", "N27 점형 블록", floor = -1),
            node("n28", "N28 점형 블록", floor = -1),
            node("stairs-lower-wangsimni-a", "왕십리역 방면 지하철 계단 A", floor = -1),
            node("n29", "N29 점형 블록", floor = -1),
            node("n30", "N30 점형 블록", floor = -1),
            node("stairs-lower-wangsimni-b", "왕십리역 방면 지하철 계단 B", floor = -1),
            node("b2-upper-suwon-entry", "수원역 방면 상단 계단 하단 점형 블록", floor = -2),
            node("b2-upper-suwon-n1", "수원역 방면 상단 승강장 연결 점형 블록", floor = -2),
            node("platform-suwon-2-2", "수원역 방면 2-2 승강장", floor = -2),
            node("platform-wangsimni-7-4-upper", "왕십리역 방면 7-4 승강장", floor = -2),
            node("b2-lower-suwon-a-entry", "수원역 방면 계단 A 하단 점형 블록", floor = -2),
            node("b2-lower-suwon-a-n1", "수원역 방면 5-4 연결 점형 블록", floor = -2),
            node("platform-suwon-5-4", "수원역 방면 5-4 승강장", floor = -2),
            node("b2-lower-suwon-b-entry", "수원역 방면 계단 B 하단 점형 블록", floor = -2),
            node("b2-lower-suwon-b-n1", "수원역 방면 7-4 연결 점형 블록", floor = -2),
            node("platform-suwon-7-4", "수원역 방면 7-4 승강장", floor = -2),
            node("b2-lower-suwon-elevator-entry", "수원역 방면 엘리베이터 하차 점형 블록", floor = -2),
            node("platform-suwon-6-4", "수원역 방면 6-4 승강장", floor = -2),
            node("b2-lower-wangsimni-a-entry", "왕십리역 방면 계단 A 하단 점형 블록", floor = -2),
            node("b2-lower-wangsimni-a-n1", "왕십리역 방면 4-1 연결 점형 블록", floor = -2),
            node("platform-wangsimni-4-1", "왕십리역 방면 4-1 승강장", floor = -2),
            node("b2-lower-wangsimni-b-entry", "왕십리역 방면 계단 B 하단 점형 블록", floor = -2),
            node("b2-lower-wangsimni-b-n1", "왕십리역 방면 2-2 연결 점형 블록", floor = -2),
            node("platform-wangsimni-2-2", "왕십리역 방면 2-2 승강장", floor = -2),
            node("b2-lower-wangsimni-elevator-entry", "왕십리역 방면 엘리베이터 하차 점형 블록", floor = -2),
            node("b2-lower-wangsimni-elevator-n1", "왕십리역 방면 3-1 연결 점형 블록", floor = -2),
            node("b2-lower-wangsimni-elevator-n2", "왕십리역 방면 3-1 앞 점형 블록", floor = -2),
            node("platform-wangsimni-3-1", "왕십리역 방면 3-1 승강장", floor = -2),
        ).associateBy(Node::id)

        val edges = buildList {
            addBidirectionalEdges("exit-3-n1", nodesById, "exit-3", "n1", 12f, 180)
            addBidirectionalEdges("n1-n2", nodesById, "n1", "n2", 20f, 180)
            addBidirectionalEdges("n2-restroom-women", nodesById, "n2", "restroom-women", 14f, 270)
            addBidirectionalEdges("n2-n3", nodesById, "n2", "n3", 10f, 180)
            addBidirectionalEdges("n3-n4", nodesById, "n3", "n4", 45f, 90)
            addBidirectionalEdges("n4-n5", nodesById, "n4", "n5", 47f, 180)
            addBidirectionalEdges("n4-n6", nodesById, "n4", "n6", 31f, 0)
            addBidirectionalEdges("n4-ticket-office", nodesById, "n4", "ticket-office", 24f, 90)
            addBidirectionalEdges("n1-n6", nodesById, "n1", "n6", 44f, 90)
            addBidirectionalEdges("n6-n7", nodesById, "n6", "n7", 38f, 0)
            addBidirectionalEdges("n6-n8", nodesById, "n6", "n8", 19f, 90)
            addBidirectionalEdges("n7-gate-upper-outside", nodesById, "n7", "gate-upper-outside", 4f, 90)
            addBidirectionalEdges("n8-exit-4", nodesById, "n8", "exit-4", 3f, 0)
            addBidirectionalEdges("n5-n9", nodesById, "n5", "n9", 44f, 270)
            addBidirectionalEdges("n5-n10", nodesById, "n5", "n10", 18f, 90)
            addBidirectionalEdges("n10-exit-5", nodesById, "n10", "exit-5", 29f, 180)
            addBidirectionalEdges("n5-n11", nodesById, "n5", "n11", 59f, 180)
            addBidirectionalEdges("n9-n12", nodesById, "n9", "n12", 1f, 180)
            addBidirectionalEdges("n12-restroom-men", nodesById, "n12", "restroom-men", 12f, 270)
            addBidirectionalEdges("n12-exit-1", nodesById, "n12", "exit-1", 26f, 180)
            addBidirectionalEdges("n11-n13", nodesById, "n11", "n13", 1f, 90)
            addBidirectionalEdges("n13-gate-lower-outside", nodesById, "n13", "gate-lower-outside", 3f, 180)
            addBidirectionalEdges(
                "gate-upper-pass",
                nodesById,
                "gate-upper-outside",
                "gate-upper-inside",
                1f,
                0,
                EdgeCategory.FARE_GATE,
            )
            addBidirectionalEdges("gate-upper-inside-n15", nodesById, "gate-upper-inside", "n15", 8f, 0)
            addBidirectionalEdges("n15-n16", nodesById, "n15", "n16", 51f, 270)
            addBidirectionalEdges("n16-stairs-upper-suwon", nodesById, "n16", "stairs-upper-suwon", 18f, 0)
            addBidirectionalEdges("n15-n17", nodesById, "n15", "n17", 12f, 90)
            addBidirectionalEdges("n17-stairs-upper-wangsimni", nodesById, "n17", "stairs-upper-wangsimni", 18f, 0)
            addBidirectionalEdges(
                "gate-lower-pass",
                nodesById,
                "gate-lower-outside",
                "gate-lower-inside",
                1f,
                180,
                EdgeCategory.FARE_GATE,
            )
            addBidirectionalEdges("gate-lower-inside-n18", nodesById, "gate-lower-inside", "n18", 11f, 180)
            addBidirectionalEdges("n18-n19", nodesById, "n18", "n19", 21f, 270)
            addBidirectionalEdges("n18-n20", nodesById, "n18", "n20", 8f, 90)
            addBidirectionalEdges("n19-n21", nodesById, "n19", "n21", 16f, 270)
            addBidirectionalEdges("n21-elevator-lower-suwon", nodesById, "n21", "elevator-lower-suwon", 1f, 270)
            addBidirectionalEdges("n21-n22", nodesById, "n21", "n22", 4f, 0)
            addBidirectionalEdges("n21-n23", nodesById, "n21", "n23", 7f, 180)
            addBidirectionalEdges("n22-n24", nodesById, "n22", "n24", 3f, 90)
            addBidirectionalEdges("n24-stairs-lower-suwon-a", nodesById, "n24", "stairs-lower-suwon-a", 7f, 0)
            addBidirectionalEdges("n23-n25", nodesById, "n23", "n25", 3f, 270)
            addBidirectionalEdges("n25-stairs-lower-suwon-b", nodesById, "n25", "stairs-lower-suwon-b", 2f, 180)
            addBidirectionalEdges("n20-elevator-lower-wangsimni", nodesById, "n20", "elevator-lower-wangsimni", 1f, 90)
            addBidirectionalEdges("n20-n26", nodesById, "n20", "n26", 3f, 0)
            addBidirectionalEdges("n20-n27", nodesById, "n20", "n27", 1f, 180)
            addBidirectionalEdges("n26-n28", nodesById, "n26", "n28", 3f, 90)
            addBidirectionalEdges("n28-stairs-lower-wangsimni-a", nodesById, "n28", "stairs-lower-wangsimni-a", 7f, 0)
            addBidirectionalEdges("n27-n29", nodesById, "n27", "n29", 5f, 180)
            addBidirectionalEdges("n29-n30", nodesById, "n29", "n30", 3f, 90)
            addBidirectionalEdges("n30-stairs-lower-wangsimni-b", nodesById, "n30", "stairs-lower-wangsimni-b", 3f, 180)
            addBidirectionalEdges(
                "stairs-upper-suwon-b2",
                nodesById,
                "stairs-upper-suwon",
                "b2-upper-suwon-entry",
                1f,
                180,
                EdgeCategory.STAIRS,
            )
            addBidirectionalEdges("b2-upper-suwon-entry-b2-upper-suwon-n1", nodesById, "b2-upper-suwon-entry", "b2-upper-suwon-n1", 3f, 0)
            addBidirectionalEdges("b2-upper-suwon-n1-platform-suwon-2-2", nodesById, "b2-upper-suwon-n1", "platform-suwon-2-2", 15f, 90)
            addBidirectionalEdges(
                "stairs-upper-wangsimni-b2",
                nodesById,
                "stairs-upper-wangsimni",
                "platform-wangsimni-7-4-upper",
                1f,
                180,
                EdgeCategory.STAIRS,
            )
            addBidirectionalEdges(
                "stairs-lower-suwon-a-b2",
                nodesById,
                "stairs-lower-suwon-a",
                "b2-lower-suwon-a-entry",
                1f,
                180,
                EdgeCategory.STAIRS,
            )
            addBidirectionalEdges("b2-lower-suwon-a-entry-b2-lower-suwon-a-n1", nodesById, "b2-lower-suwon-a-entry", "b2-lower-suwon-a-n1", 9f, 0)
            addBidirectionalEdges("b2-lower-suwon-a-n1-platform-suwon-5-4", nodesById, "b2-lower-suwon-a-n1", "platform-suwon-5-4", 15f, 90)
            addBidirectionalEdges(
                "stairs-lower-suwon-b-b2",
                nodesById,
                "stairs-lower-suwon-b",
                "b2-lower-suwon-b-entry",
                1f,
                180,
                EdgeCategory.STAIRS,
            )
            addBidirectionalEdges("b2-lower-suwon-b-entry-b2-lower-suwon-b-n1", nodesById, "b2-lower-suwon-b-entry", "b2-lower-suwon-b-n1", 5f, 180)
            addBidirectionalEdges("b2-lower-suwon-b-n1-platform-suwon-7-4", nodesById, "b2-lower-suwon-b-n1", "platform-suwon-7-4", 17f, 90)
            addBidirectionalEdges(
                "elevator-lower-suwon-b2",
                nodesById,
                "elevator-lower-suwon",
                "b2-lower-suwon-elevator-entry",
                1f,
                180,
                EdgeCategory.ELEVATOR,
            )
            addBidirectionalEdges("b2-lower-suwon-elevator-entry-platform-suwon-6-4", nodesById, "b2-lower-suwon-elevator-entry", "platform-suwon-6-4", 10f, 90)
            addBidirectionalEdges(
                "stairs-lower-wangsimni-a-b2",
                nodesById,
                "stairs-lower-wangsimni-a",
                "b2-lower-wangsimni-a-entry",
                1f,
                180,
                EdgeCategory.STAIRS,
            )
            addBidirectionalEdges("b2-lower-wangsimni-a-entry-b2-lower-wangsimni-a-n1", nodesById, "b2-lower-wangsimni-a-entry", "b2-lower-wangsimni-a-n1", 1f, 0)
            addBidirectionalEdges("b2-lower-wangsimni-a-n1-platform-wangsimni-4-1", nodesById, "b2-lower-wangsimni-a-n1", "platform-wangsimni-4-1", 17f, 270)
            addBidirectionalEdges(
                "stairs-lower-wangsimni-b-b2",
                nodesById,
                "stairs-lower-wangsimni-b",
                "b2-lower-wangsimni-b-entry",
                1f,
                180,
                EdgeCategory.STAIRS,
            )
            addBidirectionalEdges("b2-lower-wangsimni-b-entry-b2-lower-wangsimni-b-n1", nodesById, "b2-lower-wangsimni-b-entry", "b2-lower-wangsimni-b-n1", 5f, 180)
            addBidirectionalEdges("b2-lower-wangsimni-b-n1-platform-wangsimni-2-2", nodesById, "b2-lower-wangsimni-b-n1", "platform-wangsimni-2-2", 17f, 270)
            addBidirectionalEdges(
                "elevator-lower-wangsimni-b2",
                nodesById,
                "elevator-lower-wangsimni",
                "b2-lower-wangsimni-elevator-entry",
                1f,
                180,
                EdgeCategory.ELEVATOR,
            )
            addBidirectionalEdges("b2-lower-wangsimni-elevator-entry-b2-lower-wangsimni-elevator-n1", nodesById, "b2-lower-wangsimni-elevator-entry", "b2-lower-wangsimni-elevator-n1", 7f, 270)
            addBidirectionalEdges("b2-lower-wangsimni-elevator-n1-b2-lower-wangsimni-elevator-n2", nodesById, "b2-lower-wangsimni-elevator-n1", "b2-lower-wangsimni-elevator-n2", 4f, 0)
            addBidirectionalEdges("b2-lower-wangsimni-elevator-n2-platform-wangsimni-3-1", nodesById, "b2-lower-wangsimni-elevator-n2", "platform-wangsimni-3-1", 2f, 270)
        }

        val station = Station(
            id = "seohyeon-basement",
            name = "서현역 지하철역",
            nodes = nodesById.values.toList(),
            edges = edges,
            navigationPoints = listOf(
                NavigationPoint("exit-3", 180),
                NavigationPoint("restroom-women", 90),
                NavigationPoint("ticket-office", 270),
                NavigationPoint("gate-upper-outside", 270),
                NavigationPoint("gate-upper-inside", 0),
                NavigationPoint("exit-4", 180),
                NavigationPoint("exit-5", 0),
                NavigationPoint("restroom-men", 90),
                NavigationPoint("exit-1", 0),
                NavigationPoint("gate-lower-outside", 0),
                NavigationPoint("gate-lower-inside", 180),
                NavigationPoint("platform-suwon-2-2", 270),
                NavigationPoint("platform-wangsimni-7-4-upper", 90),
                NavigationPoint("platform-suwon-5-4", 270),
                NavigationPoint("platform-suwon-7-4", 270),
                NavigationPoint("platform-suwon-6-4", 270),
                NavigationPoint("platform-wangsimni-4-1", 90),
                NavigationPoint("platform-wangsimni-2-2", 90),
                NavigationPoint("platform-wangsimni-3-1", 90),
            ),
        )

        return StationMapData(
            station = station,
            rows = 58,
            columns = 24,
            nodePositions = mapOf(
                "exit-3" to MapNodePosition(10, 2),
                "n1" to MapNodePosition(12, 2),
                "n2" to MapNodePosition(15, 2),
                "restroom-women" to MapNodePosition(15, 0),
                "n3" to MapNodePosition(17, 2),
                "n4" to MapNodePosition(17, 12),
                "n5" to MapNodePosition(24, 12),
                "n6" to MapNodePosition(12, 12),
                "ticket-office" to MapNodePosition(17, 16),
                "n7" to MapNodePosition(8, 12),
                "n8" to MapNodePosition(12, 17),
                "gate-upper-outside" to MapNodePosition(8, 15),
                "gate-upper-inside" to MapNodePosition(6, 15),
                "exit-4" to MapNodePosition(10, 17),
                "n9" to MapNodePosition(24, 4),
                "n10" to MapNodePosition(24, 16),
                "exit-5" to MapNodePosition(29, 16),
                "n11" to MapNodePosition(32, 12),
                "n12" to MapNodePosition(25, 4),
                "restroom-men" to MapNodePosition(25, 0),
                "exit-1" to MapNodePosition(29, 4),
                "n13" to MapNodePosition(32, 13),
                "gate-lower-outside" to MapNodePosition(34, 13),
                "gate-lower-inside" to MapNodePosition(36, 13),
                "n15" to MapNodePosition(4, 15),
                "n16" to MapNodePosition(4, 5),
                "stairs-upper-suwon" to MapNodePosition(2, 5),
                "n17" to MapNodePosition(4, 18),
                "stairs-upper-wangsimni" to MapNodePosition(2, 18),
                "n18" to MapNodePosition(38, 13),
                "n19" to MapNodePosition(38, 9),
                "n20" to MapNodePosition(38, 18),
                "n21" to MapNodePosition(38, 6),
                "elevator-lower-suwon" to MapNodePosition(38, 4),
                "n22" to MapNodePosition(36, 6),
                "n23" to MapNodePosition(40, 6),
                "n24" to MapNodePosition(36, 8),
                "stairs-lower-suwon-a" to MapNodePosition(34, 8),
                "n25" to MapNodePosition(40, 4),
                "stairs-lower-suwon-b" to MapNodePosition(42, 4),
                "elevator-lower-wangsimni" to MapNodePosition(38, 20),
                "n26" to MapNodePosition(36, 18),
                "n27" to MapNodePosition(40, 18),
                "n28" to MapNodePosition(36, 20),
                "stairs-lower-wangsimni-a" to MapNodePosition(34, 20),
                "n29" to MapNodePosition(43, 18),
                "n30" to MapNodePosition(43, 20),
                "stairs-lower-wangsimni-b" to MapNodePosition(45, 20),
                "b2-upper-suwon-entry" to MapNodePosition(48, 5),
                "b2-upper-suwon-n1" to MapNodePosition(46, 5),
                "platform-suwon-2-2" to MapNodePosition(46, 8),
                "platform-wangsimni-7-4-upper" to MapNodePosition(48, 18),
                "b2-lower-suwon-a-entry" to MapNodePosition(50, 8),
                "b2-lower-suwon-a-n1" to MapNodePosition(48, 8),
                "platform-suwon-5-4" to MapNodePosition(48, 11),
                "b2-lower-suwon-b-entry" to MapNodePosition(52, 4),
                "b2-lower-suwon-b-n1" to MapNodePosition(54, 4),
                "platform-suwon-7-4" to MapNodePosition(54, 8),
                "b2-lower-suwon-elevator-entry" to MapNodePosition(50, 6),
                "platform-suwon-6-4" to MapNodePosition(50, 9),
                "b2-lower-wangsimni-a-entry" to MapNodePosition(50, 20),
                "b2-lower-wangsimni-a-n1" to MapNodePosition(48, 20),
                "platform-wangsimni-4-1" to MapNodePosition(48, 16),
                "b2-lower-wangsimni-b-entry" to MapNodePosition(52, 20),
                "b2-lower-wangsimni-b-n1" to MapNodePosition(54, 20),
                "platform-wangsimni-2-2" to MapNodePosition(54, 16),
                "b2-lower-wangsimni-elevator-entry" to MapNodePosition(50, 18),
                "b2-lower-wangsimni-elevator-n1" to MapNodePosition(50, 16),
                "b2-lower-wangsimni-elevator-n2" to MapNodePosition(48, 16),
                "platform-wangsimni-3-1" to MapNodePosition(48, 14),
            ),
        )
    }
}

private fun node(
    id: String,
    name: String,
    floor: Int,
): Node {
    return Node(
        id = id,
        name = name,
        floor = floor,
    )
}

private fun MutableList<Edge>.addBidirectionalEdges(
    id: String,
    nodesById: Map<String, Node>,
    firstNodeId: String,
    secondNodeId: String,
    distance: Float,
    angle: Int,
    category: EdgeCategory? = null,
) {
    val first = requireNotNull(nodesById[firstNodeId])
    val second = requireNotNull(nodesById[secondNodeId])
    add(
        Edge(
            id = "$id-forward",
            from = first,
            to = second,
            distance = distance,
            angle = angle,
            category = category,
        ),
    )
    add(
        Edge(
            id = "$id-reverse",
            from = second,
            to = first,
            distance = distance,
            angle = (angle + 180) % 360,
            category = category,
        ),
    )
}
