package a4.dogsignal.connection

internal data class DeviceConnectionUiState(
    val title: String,
    val description: String,
    val deviceCard: DeviceCardState,
    val steps: List<ConnectionStepState>,
    val actionLabel: String,
) {
    companion object {
        fun preview(): DeviceConnectionUiState = DeviceConnectionUiState(
            title = "기기연결",
            description = "센서 키트를 앱과 연결해요",
            deviceCard = DeviceCardState(
                title = "Arduino 키트 연결",
                description = "로드셀 · 초음파 · LED · 부저",
            ),
            steps = listOf(
                ConnectionStepState("패드 아래 센서판이 평평한가요?", ConnectionStepStatus.Done),
                ConnectionStepState("패드 초기 무게를 자동 보정할게요", ConnectionStepStatus.Done),
                ConnectionStepState("부저는 무음 모드로 시작해요", ConnectionStepStatus.Waiting),
            ),
            actionLabel = "기기 연결하기",
        )
    }
}

internal data class DeviceCardState(
    val title: String,
    val description: String,
)

internal data class ConnectionStepState(
    val label: String,
    val status: ConnectionStepStatus,
)

internal enum class ConnectionStepStatus {
    Done,
    Waiting,
}
