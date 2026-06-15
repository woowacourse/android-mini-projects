package a4.dogsignal.ui.home

import a4.dogsignal.data.repository.RecordRepository
import a4.dogsignal.model.Record
import a4.dogsignal.model.RecordType
import a4.dogsignal.ui.common.component.DognalTab
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.Clock

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
                    val lastDetectedDescription =
                        records.firstOrNull()
                            ?.let { formatTimeDiff(it.dateTime) }
                            ?: "오늘 감지 없음"
                    _uiState.update {
                        it.copy(
                            statusCard =
                                HomeStatusCardState(
                                    title = "오늘 상태",
                                    description = lastDetectedDescription,
                                ),
                            summaryCards = buildSummaryCards(records),
                        )
                    }
                }
                .onFailure { e ->
                    println("HomeViewModel: loadTodayRecords 실패 - ${e.message}")
                }
        }
    }

    private fun buildSummaryCards(records: List<Record>): List<HomeSummaryCardState> =
        listOf(
            HomeSummaryCardState(RecordType.URINE, records.count { it.type == RecordType.URINE }),
            HomeSummaryCardState(RecordType.STOOL, records.count { it.type == RecordType.STOOL }),
            HomeSummaryCardState(RecordType.VISIT, records.count { it.type == RecordType.VISIT }),
        )

    private fun formatTimeDiff(dateTime: LocalDateTime): String {
        val diffMinutes = (Clock.System.now() - dateTime.toInstant(TimeZone.currentSystemDefault())).inWholeMinutes
        return when {
            diffMinutes < 1 -> "방금 전"
            diffMinutes < 60 -> "마지막 감지 ${diffMinutes}분 전"
            diffMinutes < 1440 -> "마지막 감지 ${diffMinutes / 60}시간 전"
            else -> "마지막 감지 ${diffMinutes / 1440}일 전"
        }
    }

    private fun initialUiState(): HomeUiState =
        HomeUiState(
            selectedTab = DognalTab.HOME,
            statusCard = HomeStatusCardState("오늘 상태", "로딩 중..."),
            summaryCards = emptyList(),
        )
}
