package com.woowa.nureongi.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.woowa.nureongi.ui.model.MiniMapUiModel
import com.woowa.nureongi.ui.model.RouteNodeUiModel
import com.woowa.nureongi.ui.theme.NureongiColors
import com.woowa.nureongi.ui.theme.NureongiTheme
import com.woowa.nureongi.ui.theme.NureongiTypography

private const val GRID_LINE_ALPHA = 0.3f
private const val GRID_LINE_STROKE_WIDTH = 2f
private val GRID_LINE_DASH_PATTERN = floatArrayOf(10f, 10f)
private const val GRID_DOT_ALPHA = 0.5f
private const val GRID_DOT_RADIUS = 5f

private const val NODE_RADIUS = 8f
private const val HIGHLIGHTED_NODE_RADIUS = 14f
private const val HIGHLIGHTED_NODE_HOLE_RADIUS_RATIO = 0.5f
private const val ROUTE_LINE_STROKE_WIDTH = 6f
private const val LABEL_OFFSET = 6f

@Composable
fun TactileMiniMap(
    uiModel: MiniMapUiModel,
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = TextStyle(
        fontSize = NureongiTypography.ItemDescription.fontSize,
        color = NureongiColors.Accent,
    )
    val routeDescription = uiModel.path.joinToString(separator = " → ") { it.label ?: "점형 블록" }

    val infiniteTransition = rememberInfiniteTransition(label = "RadarState")
    
    // 1. 현재 위치 노드를 반짝이게 하는 레이더 파동 무한 애니메이션
    val blinkAlpha by infiniteTransition.animateFloat(
        initialValue = 0.0f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500),
            repeatMode = RepeatMode.Restart
        ),
        label = "RadarAlpha"
    )

    // 2. 가야 할 경로의 점선 흐름 애니메이션 (25f + 15f = 40f 주기)
    val dashPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "DashPhase"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(NureongiColors.Surface)
            .padding(20.dp),
    ) {
        Text(
            text = uiModel.title,
            style = NureongiTypography.ItemDescription,
            color = NureongiColors.TextSecondary,
        )
        Box(modifier = Modifier.padding(top = 12.dp)) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .semantics { contentDescription = "경로: $routeDescription" },
            ) {
                if (uiModel.rows < 2 || uiModel.columns < 2) return@Canvas

                val cellWidth = size.width / (uiModel.columns - 1)
                val cellHeight = size.height / (uiModel.rows - 1)

                drawBackgroundGrid(rows = uiModel.rows, columns = uiModel.columns, cellWidth = cellWidth, cellHeight = cellHeight)
                drawHighlightedRoute(
                    path = uiModel.path,
                    rows = uiModel.rows,
                    columns = uiModel.columns,
                    cellWidth = cellWidth,
                    cellHeight = cellHeight,
                    textMeasurer = textMeasurer,
                    labelStyle = labelStyle,
                    blinkAlpha = blinkAlpha,
                    dashPhase = dashPhase,
                )
            }
        }
    }
}

private fun cellOffset(row: Int, column: Int, cellWidth: Float, cellHeight: Float): Offset =
    Offset(x = column * cellWidth, y = row * cellHeight)

private fun DrawScope.drawBackgroundGrid(rows: Int, columns: Int, cellWidth: Float, cellHeight: Float) {
    val lineColor = NureongiColors.TextSecondary.copy(alpha = GRID_LINE_ALPHA)
    val dashedStroke = Stroke(
        width = GRID_LINE_STROKE_WIDTH,
        pathEffect = PathEffect.dashPathEffect(GRID_LINE_DASH_PATTERN),
    )

    for (row in 0 until rows) {
        drawLine(
            color = lineColor,
            start = cellOffset(row, 0, cellWidth, cellHeight),
            end = cellOffset(row, columns - 1, cellWidth, cellHeight),
            strokeWidth = dashedStroke.width,
            pathEffect = dashedStroke.pathEffect,
        )
    }
    for (column in 0 until columns) {
        drawLine(
            color = lineColor,
            start = cellOffset(0, column, cellWidth, cellHeight),
            end = cellOffset(rows - 1, column, cellWidth, cellHeight),
            strokeWidth = dashedStroke.width,
            pathEffect = dashedStroke.pathEffect,
        )
    }
    for (row in 0 until rows) {
        for (column in 0 until columns) {
            drawCircle(
                color = NureongiColors.TextSecondary.copy(alpha = GRID_DOT_ALPHA),
                radius = GRID_DOT_RADIUS,
                center = cellOffset(row, column, cellWidth, cellHeight),
            )
        }
    }
}

private fun DrawScope.drawHighlightedRoute(
    path: List<RouteNodeUiModel>,
    rows: Int,
    columns: Int,
    cellWidth: Float,
    cellHeight: Float,
    textMeasurer: TextMeasurer,
    labelStyle: TextStyle,
    blinkAlpha: Float,
    dashPhase: Float,
) {
    // 현재 사용자가 머물고 있는 위치(HIGHLIGHTED) 노드의 인덱스를 탐색
    val currentIndex = path.indexOfFirst { it.state == RouteNodeUiModel.State.HIGHLIGHTED }

    // 1. 선로(간선) 그리기
    for (index in 0 until path.size - 1) {
        val from = path[index]
        val to = path[index + 1]
        
        val startPoint = cellOffset(from.row, from.column, cellWidth, cellHeight)
        val endPoint = cellOffset(to.row, to.column, cellWidth, cellHeight)
        
        // 현재 위치 인덱스를 기준으로 이 세그먼트의 운행 상태를 판정
        val isPassed = currentIndex != -1 && index < currentIndex
        val isActiveSegment = currentIndex != -1 && index == currentIndex
        
        when {
            isPassed -> {
                // 지나온 간선: 어둡고 가느다란 정적 실선 처리
                drawLine(
                    color = NureongiColors.Accent.copy(alpha = 0.20f),
                    start = startPoint,
                    end = endPoint,
                    strokeWidth = ROUTE_LINE_STROKE_WIDTH
                )
            }
            isActiveSegment -> {
                // 현재 이동 중인 유일한 간선: 밝고 뚜렷하게 움직이는 점선 애니메이션 적용
                val dashIntervals = floatArrayOf(25f, 15f)
                val stroke = Stroke(
                    width = ROUTE_LINE_STROKE_WIDTH,
                    pathEffect = PathEffect.dashPathEffect(dashIntervals, phase = -dashPhase)
                )
                drawLine(
                    color = NureongiColors.Accent,
                    start = startPoint,
                    end = endPoint,
                    strokeWidth = stroke.width,
                    pathEffect = stroke.pathEffect
                )
            }
            else -> {
                // 예정된 간선 (index > currentIndex): 아직 진입하지 않은 정적인 중간 밝기의 실선
                drawLine(
                    color = NureongiColors.Accent.copy(alpha = 0.50f),
                    start = startPoint,
                    end = endPoint,
                    strokeWidth = ROUTE_LINE_STROKE_WIDTH
                )
            }
        }
    }
    
    // 2. 노드 및 라벨 그리기
    path.forEachIndexed { nodeIndex, node ->
        val center = cellOffset(node.row, node.column, cellWidth, cellHeight)
        val isDestination = nodeIndex == path.size - 1
        val isCurrentLocation = currentIndex != -1 && nodeIndex == currentIndex
        
        if (isCurrentLocation) {
            // 현재 위치 (HIGHLIGHTED): 반짝반짝 파동 애니메이션 (퍼져나가며 투명화)
            val maxPulseRange = 24.dp.toPx()
            val haloRadius = HIGHLIGHTED_NODE_RADIUS + (maxPulseRange * blinkAlpha)
            val haloAlpha = (1f - blinkAlpha) * 0.8f
            drawCircle(
                color = NureongiColors.Accent.copy(alpha = haloAlpha),
                radius = haloRadius,
                center = center
            )
            
            // 현재 위치 중심 원 (꽉 찬 노란색 원)
            drawCircle(
                color = NureongiColors.Accent,
                radius = HIGHLIGHTED_NODE_RADIUS,
                center = center
            )
            // 시각적 구분을 위한 코어 영역 미세 구멍
            drawCircle(
                color = NureongiColors.Background,
                radius = HIGHLIGHTED_NODE_RADIUS * 0.3f,
                center = center
            )
        }
        
        // 목적지 노드 그리기 (마지막 노드이고 현재 위치가 아닐 때, 혹은 현재 위치와 겹칠 때 테두리 유지)
        if (isDestination) {
            if (!isCurrentLocation) {
                // 목적지: 이중 원 형태 (정적)
                drawCircle(
                    color = NureongiColors.Accent,
                    radius = HIGHLIGHTED_NODE_RADIUS,
                    center = center
                )
                drawCircle(
                    color = NureongiColors.Background,
                    radius = HIGHLIGHTED_NODE_RADIUS * HIGHLIGHTED_NODE_HOLE_RADIUS_RATIO,
                    center = center
                )
            } else {
                // 현재 위치와 목적지가 같을 때 (도착 상태)
                drawCircle(
                    color = NureongiColors.Background,
                    radius = HIGHLIGHTED_NODE_RADIUS * HIGHLIGHTED_NODE_HOLE_RADIUS_RATIO,
                    center = center
                )
            }
        }
        
        // 일반 경유 노드 (목적지도 아니고 현재 위치도 아님)
        if (!isDestination && !isCurrentLocation) {
            val color = when {
                currentIndex != -1 && nodeIndex < currentIndex -> {
                    NureongiColors.Accent.copy(alpha = 0.3f) // 지나온 노드: 어둡게 반투명 처리
                }
                currentIndex != -1 && nodeIndex > currentIndex -> {
                    NureongiColors.Accent.copy(alpha = 0.7f) // 예정된 노드: 중간 밝기
                }
                else -> NureongiColors.Accent
            }
            drawCircle(color = color, radius = NODE_RADIUS, center = center)
        }
        
        // 시각장애인 편의성: 라벨이 격자망선/경로선과 겹쳐서 가독성이 훼손되지 않도록 스마트 배치 및 마스킹 적용
        node.label?.let { label ->
            val layout = textMeasurer.measure(label, style = labelStyle)
            val radius = if (isCurrentLocation || isDestination) {
                HIGHLIGHTED_NODE_RADIUS
            } else {
                NODE_RADIUS
            }

            val column = node.column
            val row = node.row
            
            // 인접한 경로선의 방향을 계산하여 라벨이 그려지지 않아야 할 방향 탐지
            var hasRightNeighbor = false
            var hasLeftNeighbor = false
            var hasUpNeighbor = false
            var hasDownNeighbor = false
            
            val idx = path.indexOf(node)
            if (idx != -1) {
                val neighbors = listOfNotNull(
                    if (idx > 0) path[idx - 1] else null,
                    if (idx < path.size - 1) path[idx + 1] else null
                )
                for (neighbor in neighbors) {
                    if (neighbor.row == row && neighbor.column > column) hasRightNeighbor = true
                    if (neighbor.row == row && neighbor.column < column) hasLeftNeighbor = true
                    if (neighbor.row < row && neighbor.column == column) hasUpNeighbor = true
                    if (neighbor.row > row && neighbor.column == column) hasDownNeighbor = true
                }
            }
            
            // 경로선이 없는 미점유 방향 및 캔버스 화면 경계를 따져 최적의 텍스트 배치 방향 결정
            val rightAvailable = !hasRightNeighbor && column < columns - 1
            val leftAvailable = !hasLeftNeighbor && column > 0
            val upAvailable = !hasUpNeighbor && row > 0
            val downAvailable = !hasDownNeighbor && row < rows - 1
            
            val direction = when {
                rightAvailable -> "RIGHT"
                leftAvailable -> "LEFT"
                upAvailable -> "UP"
                downAvailable -> "DOWN"
                else -> "RIGHT"
            }
            
            val topLeft = when (direction) {
                "RIGHT" -> Offset(center.x + radius + LABEL_OFFSET, center.y - layout.size.height / 2f)
                "LEFT" -> Offset(center.x - radius - LABEL_OFFSET - layout.size.width, center.y - layout.size.height / 2f)
                "UP" -> Offset(center.x - layout.size.width / 2f, center.y - radius - LABEL_OFFSET - layout.size.height)
                "DOWN" -> Offset(center.x - layout.size.width / 2f, center.y + radius + LABEL_OFFSET)
                else -> Offset(center.x + radius + LABEL_OFFSET, center.y - layout.size.height / 2f)
            }
            
            // 텍스트가 캔버스 바깥으로 아예 이탈하는 일을 강제 차단 (Clamping)
            val textX = topLeft.x.coerceIn(0f, size.width - layout.size.width)
            val textY = topLeft.y.coerceIn(0f, size.height - layout.size.height)
            val finalTopLeft = Offset(textX, textY)
            
            // 글자 뒤편 격자나 선들을 덮어 씌우는 마스킹 배경(RoundRect) 드로잉
            val paddingX = 4.dp.toPx()
            val paddingY = 2.dp.toPx()
            drawRoundRect(
                color = NureongiColors.Surface, // 지도 배경과 일치시켜 이질감 없는 카드 마스킹 제공
                topLeft = Offset(finalTopLeft.x - paddingX, finalTopLeft.y - paddingY),
                size = androidx.compose.ui.geometry.Size(
                    layout.size.width + paddingX * 2f,
                    layout.size.height + paddingY * 2f
                ),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
            
            drawText(
                textLayoutResult = layout,
                topLeft = finalTopLeft,
            )
        }
    }
}

@Preview
@Composable
private fun TactileMiniMapDefaultPreview() {
    NureongiTheme {
        Box(modifier = Modifier.background(NureongiColors.Background).padding(16.dp)) {
            TactileMiniMap(
                uiModel = MiniMapUiModel(
                    title = "일반 경로 (개찰구 → 갈림길 → 2번 출구)",
                    rows = 5,
                    columns = 3,
                    path = listOf(
                        RouteNodeUiModel(row = 2, column = 1, label = "개찰구", state = RouteNodeUiModel.State.PASSED),
                        RouteNodeUiModel(row = 1, column = 1, label = "갈림길", state = RouteNodeUiModel.State.HIGHLIGHTED), // 현재 위치가 갈림길
                        RouteNodeUiModel(row = 1, column = 2, label = "2번 출구", state = RouteNodeUiModel.State.NEUTRAL), // 목적지
                    )
                )
            )
        }
    }
}

@Preview
@Composable
private fun TactileMiniMapShortPreview() {
    NureongiTheme {
        Box(modifier = Modifier.background(NureongiColors.Background).padding(16.dp)) {
            TactileMiniMap(
                uiModel = MiniMapUiModel(
                    title = "짧은 경로 (출발지 → 화장실)",
                    rows = 3,
                    columns = 3,
                    path = listOf(
                        RouteNodeUiModel(row = 2, column = 0, label = "출발지", state = RouteNodeUiModel.State.HIGHLIGHTED), // 현재 출발지
                        RouteNodeUiModel(row = 2, column = 2, label = "화장실", state = RouteNodeUiModel.State.NEUTRAL), // 목적지
                    )
                )
            )
        }
    }
}

@Preview
@Composable
private fun TactileMiniMapComplexPreview() {
    NureongiTheme {
        Box(modifier = Modifier.background(NureongiColors.Background).padding(16.dp)) {
            TactileMiniMap(
                uiModel = MiniMapUiModel(
                    title = "꺾인 경로 (승강장 → 대합실 → 엘리베이터)",
                    rows = 5,
                    columns = 5,
                    path = listOf(
                        RouteNodeUiModel(row = 4, column = 1, label = "승강장", state = RouteNodeUiModel.State.PASSED),
                        RouteNodeUiModel(row = 2, column = 1, label = "대합실", state = RouteNodeUiModel.State.HIGHLIGHTED), // 현재 대합실
                        RouteNodeUiModel(row = 2, column = 3, label = "갈림길", state = RouteNodeUiModel.State.NEUTRAL),
                        RouteNodeUiModel(row = 0, column = 3, label = "엘리베이터", state = RouteNodeUiModel.State.NEUTRAL), // 목적지
                    )
                )
            )
        }
    }
}
