package a4.dogsignal.ui.record.composable

import a4.dogsignal.ui.theme.AppTheme
import a4.dogsignal.ui.theme.TextPrimary
import a4.dogsignal.ui.theme.TextSecondary
import a4.dogsignal.ui.theme.appTypography
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
internal fun RecordHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "배변 기록",
            color = TextPrimary,
            style = appTypography().headlineLarge
        )

        Text(
            text = "수정 가능한 타임라인",
            color = TextSecondary,
            style = appTypography().headlineSmall
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RecordHeaderPreview() {
    AppTheme {
        RecordHeader()
    }
}
