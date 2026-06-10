package a4.dogsignal.ui.device.composable

import a4.dogsignal.ui.theme.AppTheme
import a4.dogsignal.ui.theme.DeviceSuccessSurface
import a4.dogsignal.ui.theme.TextPrimary
import a4.dogsignal.ui.theme.TextSecondary
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dognal.shared.generated.resources.Res
import dognal.shared.generated.resources.dogFoot
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun DeviceInfoHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        PawBadge()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "도그널 시작하기",
                color = TextPrimary,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "도그널을 시작하기 위해 사용자별 ID를 입력해주세요",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun PawBadge() {
    Box(
        modifier =
            Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(DeviceSuccessSurface),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(Res.drawable.dogFoot),
            contentDescription = null,
            modifier = Modifier.size(50.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DeviceInfoHeaderPreview() {
    AppTheme {
        DeviceInfoHeader()
    }
}
