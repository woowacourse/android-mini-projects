package com.woowa.nureongi.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.woowa.nureongi.ui.theme.NureongiColors
import com.woowa.nureongi.ui.theme.NureongiTheme
import com.woowa.nureongi.ui.theme.NureongiTypography

@Composable
fun CurrentLocationBar(
    locationName: String,
    onChangeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                onClickLabel = "현재 위치 변경",
                role = Role.Button,
                onClick = onChangeClick,
            )
            .background(NureongiColors.Surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "현재 위치",
                style = NureongiTypography.ItemDescription,
                color = NureongiColors.OnAccent,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(NureongiColors.Accent)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )
            Text(
                text = locationName,
                style = NureongiTypography.ItemTitle,
                color = NureongiColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
        }
        Box(
            modifier = Modifier
                .widthIn(min = 48.dp)
                .clip(RoundedCornerShape(6.dp))
                .clearAndSetSemantics { contentDescription = "현재 위치 변경" }
                .sizeIn(minWidth = 48.dp, minHeight = 48.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "변경 ›",
                style = NureongiTypography.SectionHeader,
                color = NureongiColors.Accent,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
    }
}

@Preview
@Composable
private fun CurrentLocationBarPreview() {
    NureongiTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(NureongiColors.Background)
                .padding(20.dp),
        ) {
            CurrentLocationBar(locationName = "개찰구", onChangeClick = {})
            CurrentLocationBar(locationName = "아주아주긴출발지이름입니다", onChangeClick = {})
            CurrentLocationBar(locationName = "너무 너무 길어서 어떻게 해야할지 모르겠는 출발지 이름인데 이걸 정말 어떻게 해야할지 감이 안오네요.", onChangeClick = {})

        }
    }
}
