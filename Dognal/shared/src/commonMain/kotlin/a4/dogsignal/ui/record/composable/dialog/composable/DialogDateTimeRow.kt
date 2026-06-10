package a4.dogsignal.ui.record.composable.dialog.composable

import a4.dogsignal.ui.record.composable.dialog.ManualRecordDateTimeFormatter
import a4.dogsignal.ui.theme.Divider
import a4.dogsignal.ui.theme.TextPrimary
import a4.dogsignal.ui.theme.TextTertiary
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dognal.shared.generated.resources.Res
import dognal.shared.generated.resources.right
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun DialogDateTimeRow(
    dateTime: LocalDateTime,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        DialogDateTimeField(
            label = "날짜",
            value = ManualRecordDateTimeFormatter.formatDate(dateTime.date),
            modifier = Modifier.weight(1f),
        )
        DialogDateTimeField(
            label = "시간",
            value = ManualRecordDateTimeFormatter.formatTime(dateTime.time),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DialogDateTimeField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label,
            color = TextTertiary,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
        )
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFF8FAFC))
                    .border(1.dp, Divider, RoundedCornerShape(20.dp))
                    .padding(start = 21.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = value,
                color = TextPrimary,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                modifier = Modifier.weight(1f),
            )
            Image(
                painter = painterResource(Res.drawable.right),
                contentDescription = "오른쪽 화살표",
            )
        }
    }
}
