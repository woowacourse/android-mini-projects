package a4.dogsignal.ui.record.composable

import a4.dogsignal.ui.theme.AppTheme
import a4.dogsignal.ui.theme.TextPrimary
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AddRecordButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        colors = ButtonColors(
            containerColor = TextPrimary,
            contentColor = Color.White,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.LightGray
        ),
        modifier = modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(15.dp)
    ) {
        Text(
            text = "수동 기록 추가",
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddRecordButtonPreview() {
    AppTheme {
        AddRecordButton(onClick = {})
    }
}
