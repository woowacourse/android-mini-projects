package a4.dogsignal.ui.common.component

import a4.dogsignal.ui.theme.BrandPrimary
import a4.dogsignal.ui.theme.TextDisabled
import a4.dogsignal.ui.theme.TextPrimary
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal enum class DognalTab(val label: String) {
    HOME("홈"),
    RECORD("기록"),
}

@Composable
internal fun DognalTabs(
    selectedTab: DognalTab,
    onTabClick: (DognalTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        DognalTab.entries.forEach { tab ->
            DognalTabItem(
                label = tab.label,
                selected = tab == selectedTab,
                onClick = { onTabClick(tab) },
            )
        }
    }
}

@Composable
private fun DognalTabItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label,
            color = if (selected) TextPrimary else TextDisabled,
            style = MaterialTheme.typography.labelLarge,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            lineHeight = 16.sp,
        )
        Box(
            modifier =
                Modifier
                    .width(42.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (selected) BrandPrimary else Color.Transparent),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DognalTabsPreview() {
    DognalTabs(
        selectedTab = DognalTab.HOME,
        onTabClick = {},
    )
}
