package a4.dogsignal.ui.record.composable.dialog.composable

import a4.dogsignal.ui.theme.AppTheme
import a4.dogsignal.ui.theme.TextPrimary
import a4.dogsignal.ui.theme.TextSecondary
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun DialogHeader(
    title: String = "수동 기록 추가",
    subtitle: String = "감지되지 않은 배변을 직접 기록해요",
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            color = TextPrimary,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 23.sp,
            lineHeight = 28.sp,
        )
        Spacer(Modifier.height(7.dp))
        Text(
            text = subtitle,
            color = TextSecondary,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DialogHeaderPreview() {
    AppTheme {
        DialogHeader()
    }
}

@Preview(showBackground = true)
@Composable
private fun DialogHeaderEditPreview() {
    AppTheme {
        DialogHeader(
            title = "기록 수정",
            subtitle = "기록한 배변 내용을 수정해요",
        )
    }
}
