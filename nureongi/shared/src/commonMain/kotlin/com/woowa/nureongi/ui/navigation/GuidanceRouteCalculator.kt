package com.woowa.nureongi.ui.navigation

import com.woowa.nureongi.domain.data.StationMapData
import com.woowa.nureongi.domain.data.SeohyeonBasementStationMapData
import com.woowa.nureongi.domain.model.Route
import com.woowa.nureongi.domain.model.RouteStep
import com.woowa.nureongi.domain.service.Navigatable
import com.woowa.nureongi.domain.service.Navigator
import com.woowa.nureongi.ui.model.CurrentLocationUiModel
import com.woowa.nureongi.ui.model.DestinationItemUiModel
import com.woowa.nureongi.ui.model.GuidanceStepUiModel
import com.woowa.nureongi.ui.model.GuidanceUiState
import com.woowa.nureongi.ui.model.MiniMapUiModel
import com.woowa.nureongi.ui.model.RouteNodeUiModel

private const val TACTILE_BLOCK_GUIDE_NAME = "점형 블록"

internal fun interface GuidanceRouteCalculator {
    fun calculate(
        currentLocation: CurrentLocationUiModel,
        destination: DestinationItemUiModel,
    ): GuidanceRouteCalculationResult
}

internal sealed interface GuidanceRouteCalculationResult {
    data class Success(
        val guidanceState: GuidanceUiState,
    ) : GuidanceRouteCalculationResult

    data class Failure(
        val message: String,
    ) : GuidanceRouteCalculationResult
}

internal class MapGuidanceRouteCalculator(
    private val mapData: StationMapData = SeohyeonBasementStationMapData.getMapData(),
    private val navigator: Navigatable = Navigator(),
) : GuidanceRouteCalculator {
    override fun calculate(
        currentLocation: CurrentLocationUiModel,
        destination: DestinationItemUiModel,
    ): GuidanceRouteCalculationResult {
        if (currentLocation.nodeId == destination.id) {
            return GuidanceRouteCalculationResult.Failure(
                message = "현재 위치와 목적지가 같습니다. 다른 목적지를 선택해 주세요.",
            )
        }

        val station = mapData.station
        val startPoint = station.findNavigationPoint(currentLocation.nodeId)
        val destinationPoint = station.findNavigationPoint(destination.id)
        val destinationNode = station.findNode(destination.id)
        if (startPoint == null || destinationPoint == null || destinationNode == null) {
            return GuidanceRouteCalculationResult.Failure(
                message = "경로를 찾을 수 없습니다. 현재 위치나 목적지를 다시 선택해 주세요.",
            )
        }

        val route = runCatching {
            navigator.findRoute(
                station = station,
                from = startPoint,
                destination = destinationPoint,
            )
        }.getOrElse {
            return GuidanceRouteCalculationResult.Failure(
                message = "경로를 찾을 수 없습니다. 현재 위치나 목적지를 다시 선택해 주세요.",
            )
        }

        return GuidanceRouteCalculationResult.Success(
            guidanceState = route.toGuidanceUiState(
                destinationName = destinationNode.name,
                mapData = mapData,
            ),
        )
    }
}

private fun Route.toGuidanceUiState(
    destinationName: String,
    mapData: StationMapData,
): GuidanceUiState {
    val pathNodes = listOf(steps.first().fromNode) + steps.map(RouteStep::toNode)

    return GuidanceUiState(
        destinationName = destinationName,
        steps = steps.mapIndexed { index, step ->
            val previousAngle = if (index == 0) {
                startPoint.initialAngle
            } else {
                steps[index - 1].edge.angle
            }
            val movement = movementInstruction(previousAngle, step.edge.angle)
            val isLastStep = index == steps.lastIndex
            val targetName = if (isLastStep) {
                destinationName
            } else {
                TACTILE_BLOCK_GUIDE_NAME
            }

            GuidanceStepUiModel(
                instruction = "$movement ${formatDistance(step.edge.distance)} 이동",
                landmark = targetName,
                guideMessage = "$movement ${formatDistance(step.edge.distance)} 이동하면 ${targetName}에 도착합니다.",
                remainingDistanceText = formatDistance(step.remainingDistance),
                remainingTactileBlockText = "${steps.size - index}개",
                actionButtonText = if (isLastStep) {
                    "목적지 도착 ›"
                } else {
                    "다음 점형 블록 도착 ›"
                },
            )
        },
        arrivalGuidance = GuidanceStepUiModel(
            instruction = "도착",
            landmark = destinationName,
            guideMessage = "${destinationName}에 도착했습니다. 안내를 종료하려면 안내 종료 버튼을 누르세요.",
            remainingDistanceText = "0m",
            remainingTactileBlockText = "0개",
            actionButtonText = "안내 종료",
        ),
        miniMap = MiniMapUiModel(
            title = "${mapData.station.name} · 점자 블록 지도",
            rows = mapData.rows,
            columns = mapData.columns,
            path = pathNodes.mapIndexed { index, node ->
                val position = requireNotNull(mapData.nodePositions[node.id])
                RouteNodeUiModel(
                    row = position.row,
                    column = position.column,
                    label = destinationName.takeIf { index == pathNodes.lastIndex },
                )
            },
        ),
    )
}

private fun movementInstruction(
    currentAngle: Int,
    targetAngle: Int,
): String {
    val clockwiseDifference = (targetAngle - currentAngle + 360) % 360
    return when (clockwiseDifference) {
        in 0..30, in 330..359 -> "직진하여"
        in 31..150 -> "오른쪽으로 회전한 뒤"
        in 151..210 -> "뒤로 돌아"
        else -> "왼쪽으로 회전한 뒤"
    }
}

private fun formatDistance(distance: Float): String {
    return if (distance % 1f == 0f) {
        "${distance.toInt()}m"
    } else {
        "${distance}m"
    }
}
