package a4.dogsignal.ui.home

import a4.dogsignal.data.repository.RecordRepository
import a4.dogsignal.model.Record
import a4.dogsignal.model.RecordType
import a4.dogsignal.ui.common.component.DognalTab
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class HomeViewModel(
    private val repository: RecordRepository,
    private val deviceId: String,
) : ViewModel() {
    private val _uiState = MutableStateFlow(initialUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun refreshTodayRecords() {
        viewModelScope.launch {
            runCatching { repository.getTodayRecords(deviceId) }
                .onSuccess { records ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            lastRecordDateTime = records.firstOrNull()?.dateTime,
                            summaryCards = buildSummaryCards(records),
                        )
                    }
                }
                .onFailure { e ->
                    println("HomeViewModel: loadTodayRecords 실패 - ${e.message}")
                    _uiState.update { it.copy(isLoading = false) }
                }
        }
    }

    private fun buildSummaryCards(records: List<Record>): ImmutableList<HomeSummaryCardState> =
        persistentListOf(
            HomeSummaryCardState(RecordType.URINE, records.count { it.type == RecordType.URINE }),
            HomeSummaryCardState(RecordType.STOOL, records.count { it.type == RecordType.STOOL }),
            HomeSummaryCardState(RecordType.VISIT, records.count { it.type == RecordType.VISIT }),
        )

    private fun initialUiState(): HomeUiState =
        HomeUiState(
            selectedTab = DognalTab.HOME,
            isLoading = true,
            lastRecordDateTime = null,
            summaryCards = persistentListOf(),
        )
}
