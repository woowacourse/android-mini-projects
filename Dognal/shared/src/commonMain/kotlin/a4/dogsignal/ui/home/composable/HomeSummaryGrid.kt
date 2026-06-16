package a4.dogsignal.ui.home.composable

import a4.dogsignal.model.RecordType
import a4.dogsignal.ui.common.toColor
import a4.dogsignal.ui.common.toLabel
import a4.dogsignal.ui.home.HomeSummaryCardState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
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
import androidx.compose.foundation.layout.Spacer
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
    cards: ImmutableList<HomeSummaryCardState>,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier,
    ) {
        cards.chunked(2).forEach { rowCards ->
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                rowCards.forEach { card ->
                    HomeSummaryCard(
                        state = card,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (rowCards.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
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
        modifier =
            modifier
                .height(112.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(palette.background)
                .border(1.dp, palette.stroke, RoundedCornerShape(24.dp))
                .padding(horizontal = 22.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = state.recordType.toLabel(),
            color = palette.textColor,
            style = MaterialTheme.typography.labelLarge,
        )
        Text(
            text = "${state.count}회",
            color = palette.textColor,
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
    val textColor: Color,
)

private fun RecordType.palette(): SummaryPalette =
    when (this) {
        RecordType.URINE ->
            SummaryPalette(
                background = WarmSurface,
                stroke = WarmBorder,
                textColor = toColor(),
            )

        RecordType.STOOL ->
            SummaryPalette(
                background = CoolBackground,
                stroke = CoolStroke,
                textColor = toColor(),
            )

        RecordType.VISIT ->
            SummaryPalette(
                background = BrandSurface,
                stroke = BrandStroke,
                textColor = toColor(),
            )
    }

@Preview
@Composable
private fun HomeSummaryGridPreview() {
    AppTheme {
        HomeSummaryGrid(
            cards =
                persistentListOf(
                    HomeSummaryCardState(
                        recordType = RecordType.URINE,
                        count = 2,
                    ),
                    HomeSummaryCardState(
                        recordType = RecordType.STOOL,
                        count = 2,
                    ),
                    HomeSummaryCardState(
                        recordType = RecordType.VISIT,
                        count = 2,
                    ),
                ),
        )
    }
}
