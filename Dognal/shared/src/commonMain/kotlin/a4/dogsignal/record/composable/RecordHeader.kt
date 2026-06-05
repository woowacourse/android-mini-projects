package a4.dogsignal.record.composable

import a4.dogsignal.theme.AppTypography
import a4.dogsignal.theme.TextPrimary
import a4.dogsignal.theme.TextSecondary
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RecordHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "배변 기록",
            color = TextPrimary,
            style = AppTypography.headlineLarge
        )

        Text(
            text = "수정 가능한 타임라인",
            color = TextSecondary,
            style = AppTypography.headlineSmall
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RecordHeaderPreview() {
    RecordHeader()
}
