package a4.dogsignal.ui.connection

internal data class DeviceConnectionUiState(
    val title: String,
    val description: String,
    val deviceCard: DeviceCardState,
    val steps: List<ConnectionStepState>,
    val actionLabel: String,
)

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
