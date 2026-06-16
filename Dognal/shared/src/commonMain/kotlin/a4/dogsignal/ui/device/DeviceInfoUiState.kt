package a4.dogsignal.ui.device

import androidx.compose.runtime.Immutable

@Immutable
internal data class DeviceInfoUiState(
    val code: String = "",
    val secret: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
)
