package com.woowa.nureongi.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.woowa.nureongi.ui.theme.NureongiColors
import com.woowa.nureongi.ui.theme.NureongiTheme
import com.woowa.nureongi.ui.theme.NureongiTypography

@Composable
fun CtaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color,
    contentColor: Color,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .clickable(
                enabled = enabled,
                onClickLabel = text,
                role = Role.Button,
                onClick = onClick,
            )
            .semantics { if (!enabled) disabled() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = NureongiTypography.ItemTitle,
            color = contentColor,
        )
    }
}

@Preview
@Composable
private fun CtaButtonPreview() {
    NureongiTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(NureongiColors.Background)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CtaButton(
                text = "2번 출구까지 안내 시작",
                onClick = {},
                containerColor = NureongiColors.Accent,
                contentColor = NureongiColors.OnAccent,
            )
            CtaButton(
                text = "목적지를 선택하세요",
                onClick = {},
                enabled = false,
                containerColor = NureongiColors.Disabled,
                contentColor = NureongiColors.OnDisabled
            )
            CtaButton(
                text = "새 목적지 안내",
                onClick = {},
                containerColor = NureongiColors.NeutralSurface,
                contentColor = NureongiColors.OnAccent,
            )
        }
    }
}
