package a4.dogsignal.home

import a4.dogsignal.theme.dognalTypography
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dognal.shared.generated.resources.Res
import dognal.shared.generated.resources.bell
import dognal.shared.generated.resources.dogFoot
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun HomeScreen(
    state: HomeUiState = HomeUiState.preview(),
    onRecordClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HomeColors.Background)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 31.dp),
    ) {
        Spacer(Modifier.height(28.dp))
        HomeHeader(
            title = state.title,
            subtitle = state.subtitle,
        )
        Spacer(Modifier.height(28.dp))
        HomeTabs(
            tabs = state.tabs,
            selectedTabIndex = state.selectedTabIndex,
        )
        Spacer(Modifier.height(21.dp))
        StatusCard(state.statusCard)
        Spacer(Modifier.height(22.dp))
        SummaryGrid(state.summaryCards)
        Spacer(Modifier.weight(1f))
        RecordButton(
            label = state.actionLabel,
            onClick = onRecordClick,
            modifier = Modifier.padding(bottom = 30.dp),
        )
    }
}

@Composable
private fun HomeHeader(
    title: String,
    subtitle: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            color = HomeColors.TextPrimary,
            style = MaterialTheme.typography.headlineMedium,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 34.sp,
        )
        Text(
            text = subtitle,
            color = HomeColors.TextSecondary,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 14.sp,
            lineHeight = 21.sp,
        )
    }
}

@Composable
private fun HomeTabs(
    tabs: List<HomeTabState>,
    selectedTabIndex: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        tabs.forEachIndexed { index, tab ->
            HomeTab(
                label = tab.label,
                selected = index == selectedTabIndex,
            )
        }
    }
}

@Composable
private fun HomeTab(
    label: String,
    selected: Boolean,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label,
            color = if (selected) HomeColors.TextPrimary else HomeColors.TextMuted,
            style = MaterialTheme.typography.labelLarge,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            lineHeight = 16.sp,
        )
        Box(
            modifier = Modifier
                .width(if (selected) 69.dp else 0.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (selected) HomeColors.Green else Color.Transparent),
        )
    }
}

@Composable
private fun StatusCard(state: HomeStatusCardState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(118.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(Color.White)
            .border(1.dp, HomeColors.Stroke, RoundedCornerShape(30.dp))
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
                color = HomeColors.TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 23.sp,
            )
            Text(
                text = state.description,
                color = HomeColors.TextSecondary,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun SummaryGrid(cards: List<HomeSummaryCardState>) {
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        cards.forEach { card ->
            SummaryCard(
                state = card,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SummaryCard(
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

@Composable
private fun RecordButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(HomeColors.Dark)
            .clickable(onClick = onClick)
            .padding(horizontal = 28.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Image(
            painter = painterResource(Res.drawable.bell),
            contentDescription = "bell",
            modifier = Modifier.size(32.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Text(
                text = label,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 22.sp,
            )
            Text(
                text = "대소변을 감지할 시 LED가 켜져요",
                color = HomeColors.FooterText,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                lineHeight = 16.sp,
            )
        }
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
        background = HomeColors.WarmBackground,
        stroke = HomeColors.WarmStroke,
        label = HomeColors.WarmLabel,
        value = HomeColors.WarmText,
    )

    HomeSummaryTone.Cool -> SummaryPalette(
        background = HomeColors.CoolBackground,
        stroke = HomeColors.CoolStroke,
        label = HomeColors.CoolLabel,
        value = HomeColors.CoolText,
    )
}

@Preview
@Composable
private fun HomeScreenPreview() {
    MaterialTheme(
        typography = dognalTypography(),
    ) {
        HomeScreen()
    }
}
