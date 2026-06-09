package a4.dogsignal.ui.record.composable

import a4.dogsignal.model.Record
import a4.dogsignal.model.RecordType
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

@Composable
internal fun RecordTimelineGroup(
    recordList: List<Record>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items = recordList, key = { it.id }) { record ->
            RecordTimelineItem(
                dateTime = record.dateTime,
                recordType = record.type,
            )
        }
    }
}

@Preview
@Composable
private fun RecordTimelineGroupPreview() {
    val date = LocalDate(2026, 6, 5)
    val recordList =
        listOf(
            Record(id = "1", dateTime = LocalDateTime(date, LocalTime(2, 5)), type = RecordType.PAD),
            Record(id = "2", dateTime = LocalDateTime(date, LocalTime(1, 20)), type = RecordType.URINE),
        )
    RecordTimelineGroup(
        recordList,
    )
}
