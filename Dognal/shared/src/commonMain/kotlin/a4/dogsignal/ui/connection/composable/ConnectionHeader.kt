package a4.dogsignal.ui.connection.composable

import a4.dogsignal.ui.theme.AppTheme
import a4.dogsignal.ui.theme.TextPrimary
import a4.dogsignal.ui.theme.TextSecondary
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
internal fun ConnectionHeader(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier
    ) {
        Text(
            text = title,
            color = TextPrimary,
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = description,
            color = TextSecondary,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ConnectionHeaderPreview() {
    AppTheme {
        ConnectionHeader(
            title = "기기연결",
            description = "센서 키트를 앱과 연결해요",
        )
    }
}
