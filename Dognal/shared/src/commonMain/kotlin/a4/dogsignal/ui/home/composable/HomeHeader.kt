package a4.dogsignal.ui.home.composable

import a4.dogsignal.ui.theme.TextPrimary
import a4.dogsignal.ui.theme.TextSecondary
import a4.dogsignal.ui.theme.appTypography
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
internal fun HomeHeader(
    title: String,
    subtitle: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            color = TextPrimary,
            style = appTypography().headlineLarge
        )
        Text(
            text = subtitle,
            color = TextSecondary,
            style = appTypography().headlineSmall
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeHeaderPreview() {
    HomeHeader(
        title = "Mong의 하루",
        subtitle = "오늘의 배변·패드 상태",
    )
}
