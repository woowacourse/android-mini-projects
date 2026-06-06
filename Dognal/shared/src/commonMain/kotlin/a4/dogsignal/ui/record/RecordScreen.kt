package a4.dogsignal.ui.record

import a4.dogsignal.model.Record
import a4.dogsignal.model.RecordType
import a4.dogsignal.ui.record.composable.AddRecordButton
import a4.dogsignal.ui.record.composable.DateHeaderCard
import a4.dogsignal.ui.record.composable.RecordHeader
import a4.dogsignal.ui.record.composable.RecordTimelineGroup
import a4.dogsignal.ui.theme.RecordScreenBackground
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

@Composable
internal fun RecordScreen(
    date: LocalDate,
    recordList: List<Record>,
    onAddRecordClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = RecordScreenBackground,
        bottomBar = {
            AddRecordButton(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 38.dp),
                onClick = onAddRecordClick,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            RecordHeader(
                modifier = Modifier.padding(start = 16.dp, top = 20.dp)
            )
            Spacer(Modifier.height(30.dp))
            DateHeaderCard(date = date, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(32.dp))
            RecordTimelineGroup(
                recordList = recordList,
                modifier = Modifier.padding(start = 28.dp, end = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecordScreenPreview() {
    val date = LocalDate(2026, 6, 5)
    val recordList = listOf(
        Record(dateTime = LocalDateTime(date, LocalTime(2, 5)), type = RecordType.PAD),
        Record(dateTime = LocalDateTime(date, LocalTime(1, 20)), type = RecordType.URINE),
        Record(dateTime = LocalDateTime(date, LocalTime(1, 20)), type = RecordType.URINE),
        Record(dateTime = LocalDateTime(date, LocalTime(1, 20)), type = RecordType.STOOL),
        Record(dateTime = LocalDateTime(date, LocalTime(1, 20)), type = RecordType.URINE),
        Record(dateTime = LocalDateTime(date, LocalTime(1, 20)), type = RecordType.URINE),
        Record(dateTime = LocalDateTime(date, LocalTime(1, 20)), type = RecordType.URINE),
    )

    RecordScreen(
        date = LocalDate(2026, 6, 5),
        recordList = recordList,
        onAddRecordClick = {}
    )
}
