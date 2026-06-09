package a4.dogsignal.ui.record

import a4.dogsignal.BuildKonfig
import a4.dogsignal.data.repository.RecordRepository
import a4.dogsignal.ui.common.component.DognalTab
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class RecordViewModel(
    private val repository: RecordRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(initialUiState())
    val uiState: StateFlow<RecordUiState> = _uiState.asStateFlow()

    init {
        loadRecords()
    }

    private fun loadRecords() {
        viewModelScope.launch {
            println("RecordViewModel: DEV_USER_ID = '${BuildKonfig.DEV_USER_ID}'")
            runCatching { repository.getRecords(BuildKonfig.DEV_USER_ID) }
                .onSuccess { records ->
                    println("RecordViewModel: 불러온 기록 수 = ${records.size}")
                    _uiState.update { it.copy(recordList = records) }
                }
                .onFailure { e -> println("RecordViewModel: loadRecords 실패 - ${e.message}") }
        }
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
