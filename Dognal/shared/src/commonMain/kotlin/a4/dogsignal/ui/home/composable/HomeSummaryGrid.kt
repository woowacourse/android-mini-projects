package a4.dogsignal.ui.home.composable

import a4.dogsignal.theme.AccentOrange
import a4.dogsignal.theme.AccentPurple
import a4.dogsignal.theme.WarmBorder
import a4.dogsignal.theme.WarmSurface
import a4.dogsignal.ui.home.HomeColors
import a4.dogsignal.ui.home.HomeSummaryCardState
import a4.dogsignal.ui.home.HomeSummaryTone
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun HomeSummaryGrid(cards: List<HomeSummaryCardState>) {
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        cards.forEach { card ->
            HomeSummaryCard(
                state = card,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun HomeSummaryCard(
    state: HomeSummaryCardState,
    modifier: Modifier = Modifier,
) {
    val palette = state.tone.palette()
    Column(
        modifier = modifier
            .height(112.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(palette.background)
            .border(1.dp, palette.stroke, RoundedCornerShape(24.dp))
            .padding(horizontal = 22.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = state.label,
            color = palette.label,
            style = MaterialTheme.typography.labelLarge,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 18.sp,
        )
        Text(
            text = state.value,
            color = palette.value,
            style = MaterialTheme.typography.headlineSmall,
            fontSize = 33.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 36.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private data class SummaryPalette(
    val background: Color,
    val stroke: Color,
    val label: Color,
    val value: Color,
)

private fun HomeSummaryTone.palette(): SummaryPalette = when (this) {
    HomeSummaryTone.Warm -> SummaryPalette(
        background = WarmSurface,
        stroke = WarmBorder,
        label = HomeColors.WarmLabel,
        value = AccentOrange,
    )

    HomeSummaryTone.Cool -> SummaryPalette(
        background = HomeColors.CoolBackground,
        stroke = HomeColors.CoolStroke,
        label = HomeColors.CoolLabel,
        value = AccentPurple,
    )
}
