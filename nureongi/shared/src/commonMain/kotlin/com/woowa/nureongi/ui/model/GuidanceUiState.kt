package com.woowa.nureongi.ui.model

data class GuidanceUiState(
    val destinationName: String,
    val steps: List<GuidanceStepUiModel>,
    val currentStepIndex: Int = 0,
    val isArrived: Boolean = false,
    val arrivalGuidance: GuidanceStepUiModel,
    val miniMap: MiniMapUiModel,
) {
    init {
        require(steps.isNotEmpty())
        require(currentStepIndex in steps.indices)
    }

    val currentStep: Int
        get() = currentStepIndex + 1

    val totalSteps: Int
        get() = steps.size

    val currentGuidance: GuidanceStepUiModel
        get() = if (isArrived) arrivalGuidance else steps[currentStepIndex]

    val remainingDistanceText: String
        get() = currentGuidance.remainingDistanceText

    val remainingTactileBlockText: String
        get() = currentGuidance.remainingTactileBlockText

    val nextButtonText: String
        get() = currentGuidance.actionButtonText

    /**
     * currentStepIndex를 기준으로 miniMap의 path 노드 상태를 자동으로 계산해서 반환한다.
     * - 현재 위치 노드(currentStepIndex): HIGHLIGHTED (반짝임 애니메이션)
     * - 지나온 노드(index < currentStepIndex): PASSED (어둡게)
     * - 아직 가지 않은 노드(index > currentStepIndex): NEUTRAL (기본)
     * 단, 도착 상태이면 마지막 노드를 HIGHLIGHTED로 표시한다.
     */
    val currentMiniMap: MiniMapUiModel
        get() {
            val highlightedIndex = if (isArrived) miniMap.path.lastIndex else currentStepIndex
            val updatedPath = miniMap.path.mapIndexed { index, node ->
                val state = when {
                    index == highlightedIndex -> RouteNodeUiModel.State.HIGHLIGHTED
                    index < highlightedIndex -> RouteNodeUiModel.State.PASSED
                    else -> RouteNodeUiModel.State.NEUTRAL
                }
                node.copy(state = state)
            }
            return miniMap.copy(path = updatedPath)
        }
}

data class GuidanceStepUiModel(
    val instruction: String,
    val landmark: String,
    val guideMessage: String,
    val remainingDistanceText: String,
    val remainingTactileBlockText: String,
    val actionButtonText: String,
)

internal val PreviewGuidanceUiState = GuidanceUiState(
    destinationName = "2번 출구",
    steps = listOf(
        GuidanceStepUiModel(
            instruction = "8m 직진",
            landmark = "점형 블록",
            guideMessage = "2번 출구까지 안내를 시작합니다. 앞으로 8미터 직진하세요. 8미터 앞에 점형 블록이 있습니다.",
            remainingDistanceText = "20m",
            remainingTactileBlockText = "2개",
            actionButtonText = "다음 점형 블록 도착 ›",
        ),
        GuidanceStepUiModel(
            instruction = "오른쪽 회전",
            landmark = "점형 블록",
            guideMessage = "점형 블록에서 오른쪽으로 회전하세요.",
            remainingDistanceText = "12m",
            remainingTactileBlockText = "1개",
            actionButtonText = "다음 점형 블록 도착 ›",
        ),
        GuidanceStepUiModel(
            instruction = "12m 직진",
            landmark = "2번 출구",
            guideMessage = "앞으로 12미터 직진하면 2번 출구에 도착합니다.",
            remainingDistanceText = "12m",
            remainingTactileBlockText = "0개",
            actionButtonText = "목적지 도착 ›",
        ),
    ),
    arrivalGuidance = GuidanceStepUiModel(
        instruction = "도착",
        landmark = "2번 출구",
        guideMessage = "2번 출구에 도착했습니다. 안내를 종료하려면 안내 종료 버튼을 누르세요.",
        remainingDistanceText = "0m",
        remainingTactileBlockText = "0개",
        actionButtonText = "안내 종료",
    ),
    miniMap = MiniMapUiModel(
        title = "판교역 · 점자 블럭 지도",
        rows = 5,
        columns = 3,
        path = listOf(
            RouteNodeUiModel(row = 2, column = 1, state = RouteNodeUiModel.State.HIGHLIGHTED), // 출발지이자 현재 위치
            RouteNodeUiModel(row = 1, column = 1, state = RouteNodeUiModel.State.NEUTRAL),
            RouteNodeUiModel(row = 1, column = 2, label = "2번 출구", state = RouteNodeUiModel.State.NEUTRAL), // 목적지 (마지막 노드)
        )
    )
)
