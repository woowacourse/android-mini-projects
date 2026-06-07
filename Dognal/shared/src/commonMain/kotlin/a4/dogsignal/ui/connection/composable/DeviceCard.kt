package a4.dogsignal.ui.connection.composable

import a4.dogsignal.ui.connection.DeviceCardState
import a4.dogsignal.ui.theme.AppTheme
import a4.dogsignal.ui.theme.TextPrimary
import a4.dogsignal.ui.theme.TextSecondary
import a4.dogsignal.ui.theme.WarmBorder
import a4.dogsignal.ui.theme.WarmSurface
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dognal.shared.generated.resources.Res
import dognal.shared.generated.resources.arduino
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun DeviceCard(
    state: DeviceCardState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(132.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(WarmSurface)
                .border(
                    width = 1.dp,
                    color = WarmBorder,
                    shape = RoundedCornerShape(28.dp),
                )
                .padding(horizontal = 28.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Image(
            painter = painterResource(Res.drawable.arduino),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
        )
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = state.title,
                color = TextPrimary,
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = state.description,
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DeviceCardPreview() {
    AppTheme {
        DeviceCard(
            state =
                DeviceCardState(
                    title = "Arduino 키트 연결",
                    description = "로드셀 · 초음파",
                ),
        )
    }
}
