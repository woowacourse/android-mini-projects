package a4.dogsignal.ui.record

import a4.dogsignal.data.repository.RecordRepository
import a4.dogsignal.model.RecordType
import a4.dogsignal.ui.common.component.DognalTab
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

internal class RecordViewModel(
    private val repository: RecordRepository,
    private val deviceId: String,
) : ViewModel() {
    private val _uiState = MutableStateFlow(initialUiState())
    val uiState: StateFlow<RecordUiState> = _uiState.asStateFlow()

    private val _manualRecordSavedEvent = Channel<Unit>(Channel.BUFFERED)
    val manualRecordSavedEvent = _manualRecordSavedEvent.receiveAsFlow()

    init {
        loadRecords()
    }

    private fun loadRecords() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching { repository.getRecords(deviceId) }
                .onSuccess { records ->
                    _uiState.update { it.copy(recordList = records, isLoading = false) }
                }
                .onFailure { e ->
                    println("RecordViewModel: loadRecords 실패 - ${e.message}")
                    _uiState.update { it.copy(isLoading = false) }
                }
        }
    }

    fun saveManualRecord(
        type: RecordType,
        dateTime: LocalDateTime,
        memo: String,
    ) {
        if (_uiState.value.isSavingManualRecord) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSavingManualRecord = true,
                    manualRecordErrorMessage = null,
                )
            }
            runCatching {
                repository.createManualRecord(
                    deviceId = deviceId,
                    type = type,
                    dateTime = dateTime,
                    memo = memo,
                )
            }.onSuccess {
                val refreshedRecords = runCatching { repository.getRecords(deviceId) }.getOrNull()
                _uiState.update {
                    it.copy(
                        recordList = refreshedRecords ?: it.recordList,
                        isSavingManualRecord = false,
                        manualRecordErrorMessage = null,
                    )
                }
                _manualRecordSavedEvent.send(Unit)
            }.onFailure { e ->
                println("RecordViewModel: saveManualRecord 실패 - $e")
                _uiState.update {
                    it.copy(
                        isSavingManualRecord = false,
                        manualRecordErrorMessage = "기록 저장에 실패했어요. 잠시 후 다시 시도해주세요.",
                    )
                }
            }
        }
    }

    fun clearManualRecordError() {
        _uiState.update { it.copy(manualRecordErrorMessage = null) }
    }

    private fun initialUiState(): RecordUiState {
        /** TODO: 날짜별 필터링 **/
        return RecordUiState(
            selectedTab = DognalTab.RECORD,
            dateLabel = "전체",
            dateValue = "",
            recordList = emptyList(),
        )
    }
}
