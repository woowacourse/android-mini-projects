package a4.dogsignal.ui.home.composable

import a4.dogsignal.ui.home.HomeStatusCardState
import a4.dogsignal.ui.theme.AppTheme
import a4.dogsignal.ui.theme.Divider
import a4.dogsignal.ui.theme.TextPrimary
import a4.dogsignal.ui.theme.TextSecondary
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dognal.shared.generated.resources.Res
import dognal.shared.generated.resources.dogFoot
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun HomeStatusCard(
    state: HomeStatusCardState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(118.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(Color.White)
                .border(1.dp, Divider, RoundedCornerShape(30.dp))
                .padding(horizontal = 25.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Image(
            painter = painterResource(Res.drawable.dogFoot),
            contentDescription = "dogFoot",
            modifier = Modifier.size(64.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = state.title,
                color = TextPrimary,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = state.description,
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview
@Composable
private fun HomeStatusCardPreview() {
    AppTheme {
        HomeStatusCard(
            state =
                HomeStatusCardState(
                    title = "마지막 배변 감지 시간",
                    description = "마지막 기록 14분 전",
                ),
        )
    }
}
