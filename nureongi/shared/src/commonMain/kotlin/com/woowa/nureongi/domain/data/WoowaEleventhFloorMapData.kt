package com.woowa.nureongi.domain.data

import com.woowa.nureongi.domain.model.Edge
import com.woowa.nureongi.domain.model.NavigationPoint
import com.woowa.nureongi.domain.model.Node
import com.woowa.nureongi.domain.model.Station

object WoowaEleventhFloorMapData : StationMapDataSource {
    private val data = createMapData()

    override fun getMapData(): StationMapData = data

    private fun createMapData(): StationMapData {
        val nodesById = listOf(
            node("a", "옆 강의실"),
            node("b"),
            node("c"),
            node("d"),
            node("e", "우물가"),
            node("f"),
            node("g"),
            node("h", "자동문 안"),
            node("i", "자동문 밖"),
            node("j", "자동문 밖"),
            node("k", "자동문 안"),
            node("l", "코치실"),
            node("m"),
            node("n"),
            node("o", "수성 / 화성"),
            node("p", "금성 / 지구"),
            node("q"),
            node("r", "여자화장실"),
            node("s", "남자화장실"),
            node("t"),
            node("u", "천왕성"),
            node("v", "토성"),
            node("w", "목성"),
            node("x"),
            node("y", "큰 강의실"),
            node("z", "비상구"),
        ).associateBy(Node::id)

        val edges = buildList {
            addBidirectionalEdges("a-b", nodesById, "a", "b", 1.5f, 90)
            addBidirectionalEdges("b-c", nodesById, "b", "c", 7.5f, 90)
            addBidirectionalEdges("c-d", nodesById, "c", "d", 2.5f, 90)
            addBidirectionalEdges("e-f", nodesById, "e", "f", 1.5f, 90)
            addBidirectionalEdges("h-i", nodesById, "h", "i", 1f, 90)
            addBidirectionalEdges("i-j", nodesById, "i", "j", 7f, 90)
            addBidirectionalEdges("j-k", nodesById, "j", "k", 1f, 90)
            addBidirectionalEdges("l-m", nodesById, "l", "m", 1f, 90)
            addBidirectionalEdges("n-o", nodesById, "n", "o", 2f, 90)
            addBidirectionalEdges("o-p", nodesById, "o", "p", 5f, 90)
            addBidirectionalEdges("q-r", nodesById, "q", "r", 1.5f, 90)
            addBidirectionalEdges("r-s", nodesById, "r", "s", 7.5f, 90)
            addBidirectionalEdges("s-t", nodesById, "s", "t", 1.5f, 90)
            addBidirectionalEdges("u-v", nodesById, "u", "v", 2f, 90)
            addBidirectionalEdges("v-w", nodesById, "v", "w", 1.5f, 90)
            addBidirectionalEdges("w-x", nodesById, "w", "x", 1.5f, 90)
            addBidirectionalEdges("b-f", nodesById, "b", "f", 5.5f, 180)
            addBidirectionalEdges("d-g", nodesById, "d", "g", 3f, 180)
            addBidirectionalEdges("f-h", nodesById, "f", "h", 6f, 180)
            addBidirectionalEdges("g-k", nodesById, "g", "k", 9f, 180)
            addBidirectionalEdges("h-m", nodesById, "h", "m", 9f, 180)
            addBidirectionalEdges("k-n", nodesById, "k", "n", 6.5f, 180)
            addBidirectionalEdges("m-q", nodesById, "m", "q", 2f, 180)
            addBidirectionalEdges("n-t", nodesById, "n", "t", 4f, 180)
            addBidirectionalEdges("q-x", nodesById, "q", "x", 2.5f, 180)
            addBidirectionalEdges("c-y", nodesById, "c", "y", 1.5f, 0)
            addBidirectionalEdges("z-g", nodesById, "z", "g", 1f, 90)
        }

        val station = Station(
            id = "woowa-eleventh-floor",
            name = "우아한테크코스 11층",
            nodes = nodesById.values.toList(),
            edges = edges,
            navigationPoints = listOf(
                NavigationPoint(nodeId = "a", initialAngle = 90),
                NavigationPoint(nodeId = "e", initialAngle = 90),
                NavigationPoint(nodeId = "l", initialAngle = 90),
                NavigationPoint(nodeId = "o", initialAngle = 270),
                NavigationPoint(nodeId = "p", initialAngle = 270),
                NavigationPoint(nodeId = "r", initialAngle = 0),
                NavigationPoint(nodeId = "s", initialAngle = 0),
                NavigationPoint(nodeId = "u", initialAngle = 90),
                NavigationPoint(nodeId = "v", initialAngle = 0),
                NavigationPoint(nodeId = "w", initialAngle = 0),
                NavigationPoint(nodeId = "y", initialAngle = 0),
                NavigationPoint(nodeId = "z", initialAngle = 90),
            ),
        )

        return StationMapData(
            station = station,
            rows = 13,
            columns = 14,
            nodePositions = mapOf(
                "y" to MapNodePosition(0, 8),
                "a" to MapNodePosition(2, 3),
                "b" to MapNodePosition(2, 5),
                "c" to MapNodePosition(2, 8),
                "d" to MapNodePosition(2, 10),
                "e" to MapNodePosition(4, 3),
                "f" to MapNodePosition(4, 5),
                "z" to MapNodePosition(4, 8),
                "g" to MapNodePosition(4, 10),
                "h" to MapNodePosition(6, 5),
                "i" to MapNodePosition(6, 7),
                "j" to MapNodePosition(6, 8),
                "k" to MapNodePosition(6, 10),
                "l" to MapNodePosition(8, 3),
                "m" to MapNodePosition(8, 5),
                "n" to MapNodePosition(8, 10),
                "o" to MapNodePosition(8, 11),
                "p" to MapNodePosition(8, 13),
                "q" to MapNodePosition(10, 5),
                "r" to MapNodePosition(10, 7),
                "s" to MapNodePosition(10, 8),
                "t" to MapNodePosition(10, 10),
                "u" to MapNodePosition(12, 2),
                "v" to MapNodePosition(12, 3),
                "w" to MapNodePosition(12, 4),
                "x" to MapNodePosition(12, 5),
            ),
        )
    }
}

private fun node(
    id: String,
    name: String = "${id.uppercase()} 점형 블록",
): Node {
    return Node(
        id = id,
        name = name,
        floor = 11,
    )
}

private fun MutableList<Edge>.addBidirectionalEdges(
    id: String,
    nodesById: Map<String, Node>,
    firstNodeId: String,
    secondNodeId: String,
    distance: Float,
    angle: Int,
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
        ),
    )
    add(
        Edge(
            id = "$id-reverse",
            from = second,
            to = first,
            distance = distance,
            angle = (angle + 180) % 360,
        ),
    )
}
