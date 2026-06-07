package a4.dogsignal.ui.connection.composable

import a4.dogsignal.ui.theme.BrandPrimary
import a4.dogsignal.ui.theme.appTypography
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
internal fun ConnectButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BrandPrimary,
            contentColor = Color.White,
        ),
    ) {
        Text(
            text = label,
            style = appTypography().bodyLarge,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ConnectButtonPreview() {
    ConnectButton(
        label = "기기 연결하기",
        onClick = {},
    )
}
