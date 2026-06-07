package a4.dogsignal.ui.record.composable

import a4.dogsignal.ui.theme.AppTheme
import a4.dogsignal.ui.theme.Divider
import a4.dogsignal.ui.theme.TextPrimary
import a4.dogsignal.ui.theme.TextSecondary
import a4.dogsignal.ui.theme.appTypography
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

@Composable
internal fun DateHeaderCard(
    date: LocalDate,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(15.dp))
            .background(color = Color.White)
            .border(width = 2.dp, color = Divider, shape = RoundedCornerShape(15.dp))
            .padding(start = 25.dp, end = 42.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "오늘",
            color = TextPrimary,
            style = appTypography().labelLarge,
        )

        Text(
            text = date.toString(),
            color = TextSecondary,
            style = appTypography().labelSmall,
        )
    }
}

@Preview
@Composable
private fun DateHeaderCardPreview() {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    AppTheme {
        DateHeaderCard(date = today.date)
    }
}
