package a4.dogsignal.ui.connection.composable

import a4.dogsignal.ui.theme.TextPrimary
import a4.dogsignal.ui.theme.TextSecondary
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun ConnectionHeader(
    title: String,
    description: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = title,
            color = TextPrimary,
            style = MaterialTheme.typography.headlineMedium,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 34.sp,
        )
        Text(
            text = description,
            color = TextSecondary,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 14.sp,
            lineHeight = 21.sp,
        )
    }
}
