package com.woowa.nureongi.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.woowa.nureongi.ui.theme.NureongiColors
import com.woowa.nureongi.ui.theme.NureongiTheme
import com.woowa.nureongi.ui.theme.NureongiTypography.StatLabelStyle
import com.woowa.nureongi.ui.theme.NureongiTypography.StatValueStyle

@Composable
fun StatTile(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(NureongiColors.Surface)
            .padding(horizontal = 20.dp, vertical = 18.dp)
            .clearAndSetSemantics {
                contentDescription = "$label $value"
            },
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = value,
            style = StatValueStyle,
            color = NureongiColors.Accent,
        )
        Text(
            text = label,
            style = StatLabelStyle,
            color = NureongiColors.TextSecondary,
        )
    }
}

@Preview
@Composable
private fun StatTilePreview() {
    NureongiTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(NureongiColors.Background)
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatTile(value = "20m", label = "남은 거리", modifier = Modifier.weight(1f))
            StatTile(value = "2개", label = "남은 점형 블럭", modifier = Modifier.weight(1f))
        }
    }
}
