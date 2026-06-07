package a4.dogsignal.ui.home.composable

import a4.dogsignal.model.RecordType
import a4.dogsignal.ui.home.HomeSummaryCardState
import a4.dogsignal.ui.common.toColor
import a4.dogsignal.ui.common.toLabel
import a4.dogsignal.ui.theme.AppTheme
import a4.dogsignal.ui.theme.BrandStroke
import a4.dogsignal.ui.theme.BrandSurface
import a4.dogsignal.ui.theme.CoolBackground
import a4.dogsignal.ui.theme.CoolStroke
import a4.dogsignal.ui.theme.WarmBorder
import a4.dogsignal.ui.theme.WarmSurface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun HomeSummaryGrid(
    cards: List<HomeSummaryCardState>,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier,
    ) {
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
    val palette = state.recordType.palette()
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
            text = state.recordType.toLabel(),
            color = palette.label,
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = "${state.count}회",
            color = palette.value,
            fontSize = 36.sp,
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

private fun RecordType.palette(): SummaryPalette = when (this) {
    RecordType.URINE -> SummaryPalette(
        background = WarmSurface,
        stroke = WarmBorder,
        label = toColor(),
        value = toColor(),
    )

    RecordType.STOOL -> SummaryPalette(
        background = CoolBackground,
        stroke = CoolStroke,
        label = toColor(),
        value = toColor(),
    )

    RecordType.PAD -> SummaryPalette(
        background = BrandSurface,
        stroke = BrandStroke,
        label = toColor(),
        value = toColor(),
    )
}

@Preview
@Composable
private fun HomeSummaryGridPreview() {
    AppTheme {
        HomeSummaryGrid(
            cards = listOf(
                HomeSummaryCardState(
                    recordType = RecordType.URINE,
                    count = 2,
                ),
                HomeSummaryCardState(
                    recordType = RecordType.STOOL,
                    count = 2,
                ),
                HomeSummaryCardState(
                    recordType = RecordType.PAD,
                    count = 2,
                ),
            ),
        )
    }
}
