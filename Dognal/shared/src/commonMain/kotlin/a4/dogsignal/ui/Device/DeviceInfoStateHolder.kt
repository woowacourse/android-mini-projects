package a4.dogsignal.ui.Device

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

internal class DeviceInfoStateHolder
{
    var state by mutableStateOf(DeviceInfoUiState())
        private set

    fun onUserIdChange(deviceId: String) {
        state =
            state.copy(
                id = deviceId,
            )
    }
}
