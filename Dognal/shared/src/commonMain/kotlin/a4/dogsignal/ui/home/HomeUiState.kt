package a4.dogsignal.ui.home

import a4.dogsignal.model.RecordType
import a4.dogsignal.ui.common.component.DognalTab
import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.LocalDateTime

@Immutable
internal data class HomeUiState(
    val selectedTab: DognalTab,
    val isLoading: Boolean,
    val lastRecordDateTime: LocalDateTime?,
    val summaryCards: ImmutableList<HomeSummaryCardState>,
)

@Immutable
internal data class HomeSummaryCardState(
    val recordType: RecordType,
    val count: Int,
)
