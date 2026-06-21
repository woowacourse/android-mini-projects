package com.woowa.nureongi.ui.navigation

import com.woowa.nureongi.domain.data.StationMapDataSource
import com.woowa.nureongi.domain.data.SeohyeonBasementStationMapData
import com.woowa.nureongi.ui.model.CurrentLocationUiModel
import com.woowa.nureongi.ui.model.DestinationItemUiModel
import com.woowa.nureongi.ui.model.DestinationSelectionUiState
import com.woowa.nureongi.ui.model.GuidanceStepUiModel
import com.woowa.nureongi.ui.model.GuidanceUiState
import com.woowa.nureongi.ui.model.MiniMapUiModel
import com.woowa.nureongi.ui.model.PlaceUiModel
import com.woowa.nureongi.ui.model.RouteNodeUiModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class NureongiAppViewModelTest {
    @Test
    fun `하나의 MapData를 화면 목록과 경로 서비스가 함께 사용한다`() {
        var loadCount = 0
        val mapDataSource = StationMapDataSource {
            loadCount += 1
            SeohyeonBasementStationMapData.getMapData()
        }
        val viewModel = NureongiAppViewModel(mapDataSource = mapDataSource)

        val state = viewModel.uiState.value
        assertEquals(
            state.currentLocationState.locations.map { it.id },
            state.destinationState.destinations.map { it.id },
        )
        viewModel.onLocationSelected("exit-3")
        viewModel.onDestinationSelected("platform-suwon-2-2")
        viewModel.onStartGuidance()

        assertEquals(1, loadCount)
        assertNull(viewModel.uiState.value.guidanceState?.miniMap?.path?.get(1)?.label)
    }

    @Test
    fun `초기 상태는 현재 위치와 목적지가 선택되지 않아 안내를 시작할 수 없다`() {
        val state = NureongiAppViewModel().uiState.value

        assertNull(state.currentLocationState.selectedLocationId)
        assertNull(state.destinationState.currentLocation)
        assertNull(state.destinationState.selectedDestinationId)
        assertEquals("현재 위치를 선택해 주세요", state.destinationState.currentLocationName)
        assertEquals("목적지를 선택하세요", state.destinationState.startGuidanceButtonText)
        assertEquals(false, state.destinationState.canStartGuidance)
    }

    @Test
    fun `현재 위치를 선택하면 목적지 화면에서 사용할 위치 상태를 갱신한다`() {
        val viewModel = NureongiAppViewModel()

        val currentLocation = viewModel.onLocationSelected("exit-3")

        val state = viewModel.uiState.value
        val expectedName = SeohyeonBasementStationMapData.getMapData().station.findNode("exit-3")!!.name
        assertEquals("exit-3", currentLocation?.nodeId)
        assertEquals("exit-3", state.currentLocationState.selectedLocationId)
        assertEquals("exit-3", state.destinationState.currentLocation?.nodeId)
        assertEquals(expectedName, state.destinationState.currentLocationName)
    }

    @Test
    fun `존재하지 않는 위치와 목적지 선택은 상태에 반영하지 않는다`() {
        val viewModel = NureongiAppViewModel()

        val currentLocation = viewModel.onLocationSelected("unknown")
        viewModel.onDestinationSelected("unknown")

        val state = viewModel.uiState.value
        assertNull(currentLocation)
        assertNull(state.currentLocationState.selectedLocationId)
        assertNull(state.destinationState.selectedDestinationId)
    }

    @Test
    fun `목적지를 선택하고 안내를 시작하면 계산된 안내 상태를 저장한다`() {
        val expectedGuidance = guidanceState(destinationName = "2번 출구")
        val calculator = RecordingRouteCalculator(
            result = GuidanceRouteCalculationResult.Success(expectedGuidance),
        )
        val viewModel = NureongiAppViewModel(routeCalculator = calculator)

        viewModel.onLocationSelected("exit-3")
        viewModel.onDestinationSelected("platform-suwon-2-2")
        val request = viewModel.onStartGuidance()

        val state = viewModel.uiState.value
        assertEquals(
            GuidanceNavRoute(
                currentLocationId = "exit-3",
                destinationId = "platform-suwon-2-2",
            ),
            request,
        )
        assertEquals(expectedGuidance, state.guidanceState)
        assertEquals("exit-3", calculator.currentLocation?.nodeId)
        assertEquals("platform-suwon-2-2", calculator.destination?.id)
        assertNull(state.destinationState.error)
    }

    @Test
    fun `경로 계산에 실패하면 목적지 화면에 오류를 표시한다`() {
        val calculator = RecordingRouteCalculator(
            result = GuidanceRouteCalculationResult.Failure("경로 없음"),
        )
        val viewModel = NureongiAppViewModel(routeCalculator = calculator)

        viewModel.onLocationSelected("exit-3")
        viewModel.onDestinationSelected("platform-suwon-2-2")
        val request = viewModel.onStartGuidance()

        val state = viewModel.uiState.value
        assertNull(request)
        assertEquals("경로 없음", state.destinationState.error?.message)
        assertNull(state.guidanceState)
    }

    @Test
    fun `로딩 중에는 화면 선택 이벤트를 무시한다`() {
        val initialState = NureongiAppUiState(
            currentLocationState = NureongiAppUiState()
                .currentLocationState
                .copy(isLoading = true),
            destinationState = DestinationSelectionUiState(isLoading = true),
        )
        val viewModel = NureongiAppViewModel(initialState = initialState)

        val currentLocation = viewModel.onLocationSelected("exit-3")
        viewModel.onDestinationSelected("platform-suwon-2-2")
        val request = viewModel.onStartGuidance()

        val state = viewModel.uiState.value
        assertNull(currentLocation)
        assertNull(request)
        assertNull(state.currentLocationState.selectedLocationId)
        assertNull(state.destinationState.selectedDestinationId)
        assertNull(state.guidanceState)
    }

    @Test
    fun `안내 destination에 직접 진입하면 ID로 안내 상태를 복원한다`() {
        val viewModel = NureongiAppViewModel(
            routeCalculator = RecordingRouteCalculator(
                result = GuidanceRouteCalculationResult.Success(guidanceState()),
            ),
        )

        viewModel.onGuidanceDestinationEntered(
            currentLocationId = "exit-3",
            destinationId = "platform-suwon-2-2",
        )

        assertNotNull(viewModel.uiState.value.guidanceState)
    }

    @Test
    fun `다음 안내 단계로 진행하고 마지막 단계 이후 도착 상태가 된다`() {
        val initialGuidance = guidanceState().copy(
            steps = listOf(
                guidanceStep("첫 번째"),
                guidanceStep("두 번째"),
            ),
        )
        val viewModel = NureongiAppViewModel(
            routeCalculator = RecordingRouteCalculator(
                result = GuidanceRouteCalculationResult.Success(initialGuidance),
            ),
        )
        viewModel.onLocationSelected("exit-3")
        viewModel.onDestinationSelected("platform-suwon-2-2")
        viewModel.onStartGuidance()

        viewModel.onNextGuidanceStep()
        assertEquals(1, viewModel.uiState.value.guidanceState?.currentStepIndex)

        viewModel.onNextGuidanceStep()
        assertEquals(true, viewModel.uiState.value.guidanceState?.isArrived)
    }

    @Test
    fun `현재 위치와 같은 목적지를 선택하면 경로 계산 오류를 반환한다`() {
        val calculator = MapGuidanceRouteCalculator()

        val result = calculator.calculate(
            currentLocation = CurrentLocationUiModel("exit-3", "옆 강의실"),
            destination = DestinationItemUiModel(
                id = "exit-3",
                place = PlaceUiModel("옆 강의실", ""),
            ),
        )

        val failure = assertIs<GuidanceRouteCalculationResult.Failure>(result)
        assertEquals(
            "현재 위치와 목적지가 같습니다. 다른 목적지를 선택해 주세요.",
            failure.message,
        )
    }

    @Test
    fun `등록된 위치와 목적지의 경로를 안내 상태로 계산한다`() {
        val calculator = MapGuidanceRouteCalculator()

        val result = calculator.calculate(
            currentLocation = CurrentLocationUiModel("exit-3", "start"),
            destination = DestinationItemUiModel(
                id = "platform-suwon-2-2",
                place = PlaceUiModel("destination", ""),
            ),
        )

        val success = assertIs<GuidanceRouteCalculationResult.Success>(result)
        val station = SeohyeonBasementStationMapData.getMapData().station
        assertEquals(station.findNode("platform-suwon-2-2")!!.name, success.guidanceState.destinationName)
        assertNull(success.guidanceState.miniMap.path.first().label)
        assertNull(success.guidanceState.miniMap.path[1].label)
        assertEquals(station.findNode("platform-suwon-2-2")!!.name, success.guidanceState.miniMap.path.last().label)
        assertEquals(11, success.guidanceState.steps.size)
        assertEquals("점형 블록", success.guidanceState.steps.first().landmark)
        assertEquals(station.findNode("platform-suwon-2-2")!!.name, success.guidanceState.steps.last().landmark)
        assertEquals(false, success.guidanceState.steps.first().guideMessage.contains(station.findNode("n1")!!.name))
        assertEquals(true, success.guidanceState.steps[0].instruction.contains("12m"))
        assertEquals(true, success.guidanceState.steps[1].instruction.contains("44m"))
    }

    @Test
    fun `목적지와 현재 위치를 동일하게 선택하는 즉시 화면에 오류를 노출한다`() {
        val viewModel = NureongiAppViewModel()

        viewModel.onLocationSelected("exit-3")
        viewModel.onDestinationSelected("exit-3")

        val state = viewModel.uiState.value
        assertEquals(
            "현재 위치와 목적지가 같습니다. 다른 목적지를 선택해 주세요.",
            state.destinationState.error?.message,
        )
        assertEquals(false, state.destinationState.canStartGuidance)
    }

    @Test
    fun `목적지와 현재 위치가 같은 상태에서 서로 다른 목적지로 바꾸면 오류가 즉시 해제된다`() {
        val viewModel = NureongiAppViewModel()

        viewModel.onLocationSelected("exit-3")
        viewModel.onDestinationSelected("exit-3")
        assertEquals(
            "현재 위치와 목적지가 같습니다. 다른 목적지를 선택해 주세요.",
            viewModel.uiState.value.destinationState.error?.message,
        )

        // 다른 목적지 선택
        viewModel.onDestinationSelected("platform-suwon-2-2")
        assertNull(viewModel.uiState.value.destinationState.error)
        assertEquals(true, viewModel.uiState.value.destinationState.canStartGuidance)
    }

    @Test
    fun `voice location selection state is managed by viewmodel`() {
        val viewModel = NureongiAppViewModel()
        val firstLocation = viewModel.uiState.value.currentLocationState.locations.first()

        viewModel.onVoiceLocationSelectionStarted()

        assertEquals(true, viewModel.uiState.value.currentLocationState.isVoiceListening)
        assertEquals(
            "출발지를 말씀해주세요.",
            viewModel.uiState.value.currentLocationState.voiceSelectionMessage,
        )

        val selectedLocation = viewModel.onVoiceLocationRecognized(listOf(firstLocation.place.name))

        val state = viewModel.uiState.value
        assertEquals(firstLocation.id, selectedLocation?.nodeId)
        assertEquals(firstLocation.id, state.currentLocationState.selectedLocationId)
        assertEquals(false, state.currentLocationState.isVoiceListening)
        assertEquals(
            "${firstLocation.place.name}을 현재 위치로 설정합니다.",
            state.currentLocationState.voiceSelectionMessage,
        )
    }

    @Test
    fun `voice location selection failure updates viewmodel state`() {
        val viewModel = NureongiAppViewModel()

        viewModel.onVoiceLocationSelectionStarted()
        val selectedLocation = viewModel.onVoiceLocationRecognized(listOf("unknown voice input"))

        val state = viewModel.uiState.value.currentLocationState
        assertNull(selectedLocation)
        assertEquals(false, state.isVoiceListening)
        assertEquals(
            "출발지를 찾지 못했습니다. 다시 말씀해주세요.",
            state.voiceSelectionMessage,
        )
    }

    @Test
    fun `voice location recognition error updates viewmodel state`() {
        val viewModel = NureongiAppViewModel()

        viewModel.onVoiceLocationSelectionStarted()
        viewModel.onVoiceLocationRecognitionFailed("microphone permission required")

        val state = viewModel.uiState.value.currentLocationState
        assertEquals(false, state.isVoiceListening)
        assertEquals("microphone permission required", state.voiceSelectionMessage)
    }
}

private class RecordingRouteCalculator(
    private val result: GuidanceRouteCalculationResult,
) : GuidanceRouteCalculator {
    var currentLocation: CurrentLocationUiModel? = null
        private set
    var destination: DestinationItemUiModel? = null
        private set

    override fun calculate(
        currentLocation: CurrentLocationUiModel,
        destination: DestinationItemUiModel,
    ): GuidanceRouteCalculationResult {
        this.currentLocation = currentLocation
        this.destination = destination
        return result
    }
}

private fun guidanceState(
    destinationName: String = "목적지",
): GuidanceUiState {
    val step = GuidanceStepUiModel(
        instruction = "직진",
        landmark = destinationName,
        guideMessage = "$destinationName 방향으로 직진하세요.",
        remainingDistanceText = "8m",
        remainingTactileBlockText = "1개",
        actionButtonText = "목적지 도착 ›",
    )
    return GuidanceUiState(
        destinationName = destinationName,
        steps = listOf(step),
        arrivalGuidance = step.copy(
            instruction = "도착",
            remainingDistanceText = "0m",
            remainingTactileBlockText = "0개",
            actionButtonText = "안내 종료",
        ),
        miniMap = MiniMapUiModel(
            title = "테스트 지도",
            rows = 1,
            columns = 1,
            path = listOf(RouteNodeUiModel(0, 0, destinationName)),
        ),
    )
}

private fun guidanceStep(instruction: String): GuidanceStepUiModel {
    return GuidanceStepUiModel(
        instruction = instruction,
        landmark = "목적지",
        guideMessage = instruction,
        remainingDistanceText = "8m",
        remainingTactileBlockText = "1개",
        actionButtonText = "다음",
    )
}
