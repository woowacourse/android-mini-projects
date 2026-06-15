package a4.dogsignal.ui.registration

internal data class DeviceRegistrationUiState(
    val deviceCard: DeviceRegistrationCardState,
    val steps: List<DeviceRegistrationStepState>,
    val actionLabel: String,
)

internal data class DeviceRegistrationCardState(
    val title: String,
    val description: String,
)

internal data class DeviceRegistrationStepState(
    val label: String,
    val status: DeviceRegistrationStepStatus,
)

internal enum class DeviceRegistrationStepStatus {
    Done,
    Waiting,
}
