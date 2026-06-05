package a4.dogsignal.ui

import a4.dogsignal.ui.theme.DognalColors
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun DognalTabs(
    tabs: List<DognalTabState>,
    selectedTabIndex: Int,
    onTabClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        tabs.forEachIndexed { index, tab ->
            DognalTab(
                label = tab.label,
                selected = index == selectedTabIndex,
                onClick = { onTabClick(index) },
            )
        }
    }
}

@Composable
private fun DognalTab(
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
            color = if (selected) DognalColors.TextPrimary else DognalColors.TextTertiary,
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
                .background(if (selected) DognalColors.Primary else Color.Transparent),
        )
    }
}
