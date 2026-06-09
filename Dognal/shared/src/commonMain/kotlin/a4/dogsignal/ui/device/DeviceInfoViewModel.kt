package a4.dogsignal.ui.device

import a4.dogsignal.data.repository.DeviceRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class DeviceInfoViewModel(
    private val deviceRepository: DeviceRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DeviceInfoUiState())
    val uiState: StateFlow<DeviceInfoUiState> = _uiState.asStateFlow()

    private val _navigationEvent = Channel<String>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    fun onCodeChange(code: String) {
        _uiState.update { it.copy(code = code, error = null) }
    }

    fun onSecretChange(secret: String) {
        _uiState.update { it.copy(secret = secret, error = null) }
    }

    fun onSubmit() {
        val state = _uiState.value
        if (state.code.isBlank() || state.secret.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { deviceRepository.authenticate(state.code, state.secret) }
                .onSuccess { deviceId -> _navigationEvent.send(deviceId) }
                .onFailure {
                    _uiState.update {
                        it.copy(isLoading = false, error = "코드 또는 시크릿이 올바르지 않습니다.")
                    }
                }
        }
    }
}
