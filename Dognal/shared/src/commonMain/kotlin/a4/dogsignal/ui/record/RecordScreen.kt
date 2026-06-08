package a4.dogsignal.ui.record

import a4.dogsignal.model.Record
import a4.dogsignal.model.RecordType
import a4.dogsignal.ui.common.component.DognalTab
import a4.dogsignal.ui.common.component.DognalTabs
import a4.dogsignal.ui.common.component.ScreenHeader
import a4.dogsignal.ui.record.composable.AddRecordButton
import a4.dogsignal.ui.record.composable.DateHeaderCard
import a4.dogsignal.ui.record.composable.RecordTimelineGroup
import a4.dogsignal.ui.theme.AppTheme
import a4.dogsignal.ui.theme.RecordScreenBackground
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

@Composable
internal fun RecordScreen(
    state: RecordUiState,
    onAddRecordClick: () -> Unit,
    onTabClick: (DognalTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = RecordScreenBackground,
        bottomBar = {
            AddRecordButton(
                modifier =
                    Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 38.dp),
                onClick = onAddRecordClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier.padding(innerPadding).padding(horizontal = 31.dp),
        ) {
            ScreenHeader(
                title = "배변 기록",
                subtitle = "수정 가능한 타임라인",
                modifier = Modifier.padding(top = 28.dp),
            )
            Spacer(Modifier.height(28.dp))
            DognalTabs(
                selectedTab = state.selectedTab,
                onTabClick = onTabClick,
                modifier = Modifier.align(Alignment.CenterHorizontally).width(250.dp),
            )
            Spacer(Modifier.height(30.dp))
            DateHeaderCard(
                label = state.dateLabel,
                date = state.dateValue,
            )
            Spacer(Modifier.height(32.dp))
            RecordTimelineGroup(
                recordList = state.recordList,
                modifier = Modifier.padding(start = 15.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecordScreenPreview() {
    val date = LocalDate(2026, 6, 5)
    AppTheme {
        RecordScreen(
            state =
                RecordUiState(
                    selectedTab = DognalTab.RECORD,
                    dateLabel = "오늘",
                    dateValue = date.toString(),
                    recordList =
                        listOf(
                            Record(
                                id = "1",
                                dateTime = LocalDateTime(date, LocalTime(2, 5)),
                                type = RecordType.PAD,
                            ),
                            Record(
                                id = "2",
                                dateTime = LocalDateTime(date, LocalTime(1, 20)),
                                type = RecordType.URINE,
                            ),
                            Record(
                                id = "3",
                                dateTime = LocalDateTime(date, LocalTime(1, 20)),
                                type = RecordType.URINE,
                            ),
                            Record(
                                id = "4",
                                dateTime = LocalDateTime(date, LocalTime(1, 20)),
                                type = RecordType.STOOL,
                            ),
                            Record(
                                id = "5",
                                dateTime = LocalDateTime(date, LocalTime(1, 20)),
                                type = RecordType.URINE,
                            ),
                            Record(
                                id = "6",
                                dateTime = LocalDateTime(date, LocalTime(1, 20)),
                                type = RecordType.URINE,
                            ),
                            Record(
                                id = "7",
                                dateTime = LocalDateTime(date, LocalTime(1, 20)),
                                type = RecordType.URINE,
                            ),
                        ),
                ),
            onAddRecordClick = {},
            onTabClick = {},
        )
    }
}
