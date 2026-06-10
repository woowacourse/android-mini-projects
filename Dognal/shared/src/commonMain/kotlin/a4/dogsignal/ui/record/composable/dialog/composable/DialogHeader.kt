package a4.dogsignal.ui.record.composable.dialog.composable

import a4.dogsignal.ui.theme.TextPrimary
import a4.dogsignal.ui.theme.TextSecondary
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun DialogHeader(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "수동 기록 추가",
            color = TextPrimary,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 23.sp,
            lineHeight = 28.sp,
        )
        Spacer(Modifier.height(7.dp))
        Text(
            text = "감지되지 않은 배변을 직접 기록해요",
            color = TextSecondary,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
