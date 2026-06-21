package com.woowa.nureongi.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.woowa.nureongi.ui.model.PlaceUiModel
import com.woowa.nureongi.ui.theme.NureongiColors
import com.woowa.nureongi.ui.theme.NureongiTheme
import com.woowa.nureongi.ui.theme.NureongiTypography

@Composable
fun PlaceListItem(
    place: PlaceUiModel,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor = if (selected) NureongiColors.SelectedSurface else NureongiColors.Surface
    val borderColor = if (selected) NureongiColors.Accent else NureongiColors.SurfaceBorder
    val iconBackground = if (selected) NureongiColors.Accent else NureongiColors.IconSurface
    val dotColor = if (selected) NureongiColors.OnAccent else NureongiColors.Accent

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton,
            )
            .semantics {
                this.selected = selected
            }
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BrailleIcon(
            backgroundColor = iconBackground,
            dotColor = dotColor,
            modifier = Modifier.size(56.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = place.name,
                style = NureongiTypography.ItemTitle,
                color = NureongiColors.TextPrimary,
            )
            Text(
                text = place.location,
                style = NureongiTypography.ItemDescription,
                color = NureongiColors.TextSecondary,
            )
        }
    }
}

@Preview
@Composable
private fun PlaceListItemPreview() {
    NureongiTheme {
        Column(
            modifier = Modifier
                .background(NureongiColors.Background)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            var selectedIndex by remember { mutableStateOf(1) }
            val places = listOf(
                PlaceUiModel("1번 출구", "지상 · 버스정류장 방면"),
                PlaceUiModel("2번 출구", "지상 · 광장 방면"),
                PlaceUiModel("화장실", "대합실 왼쪽"),
            )
            places.forEachIndexed { index, place ->
                PlaceListItem(
                    place = place,
                    selected = index == selectedIndex,
                    onClick = { selectedIndex = index },
                )
            }
        }
    }
}
