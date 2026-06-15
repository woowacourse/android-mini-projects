package a4.dogsignal.ui.registration

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
internal data class DeviceRegistrationUiState(
    val deviceCard: DeviceRegistrationCardState,
    val steps: ImmutableList<DeviceRegistrationStepState>,
    val actionLabel: String,
)

@Immutable
internal data class DeviceRegistrationCardState(
    val title: String,
    val description: String,
)

@Immutable
internal data class DeviceRegistrationStepState(
    val label: String,
    val status: DeviceRegistrationStepStatus,
)

internal enum class DeviceRegistrationStepStatus {
    Done,
    Waiting,
}
