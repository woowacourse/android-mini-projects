package a4.dogsignal.ui.record.composable

import a4.dogsignal.model.RecordType
import a4.dogsignal.ui.common.toColor
import a4.dogsignal.ui.common.toLabel
import a4.dogsignal.ui.theme.AppTheme
import a4.dogsignal.ui.theme.BorderLight
import a4.dogsignal.ui.theme.Divider
import a4.dogsignal.ui.theme.TextPrimary
import a4.dogsignal.ui.theme.TextSecondary
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number

@Composable
internal fun RecordTimelineItem(
    dateTime: LocalDateTime,
    recordType: RecordType,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().height(100.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(recordType.toColor()),
            )
            Box(
                modifier =
                    Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(BorderLight),
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "${dateTime.month.number}월 ${dateTime.day}일 " +
                    "${dateTime.hour}:${dateTime.minute.toString().padStart(2, '0')}",
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
            )

            Box(
                modifier =
                    Modifier
                        .height(64.dp)
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(20.dp))
                        .border(width = 1.dp, color = Divider, shape = RoundedCornerShape(20.dp))
                        .background(color = Color.White)
                        .padding(start = 22.dp),
            ) {
                Text(
                    text = recordType.toLabel(),
                    color = TextPrimary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 14.dp),
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
)
@Composable
private fun RecordTimelineItemPreview() {
    AppTheme {
        RecordTimelineItem(
            dateTime = LocalDateTime(2026, 6, 9, 14, 44),
            recordType = RecordType.PAD,
        )
    }
}
