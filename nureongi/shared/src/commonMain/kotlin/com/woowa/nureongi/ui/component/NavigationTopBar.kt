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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.woowa.nureongi.ui.theme.NureongiColors
import com.woowa.nureongi.ui.theme.NureongiTheme
import com.woowa.nureongi.ui.theme.NureongiTypography

@Composable
fun NavigationTopBar(
    destinationName: String,
    currentStep: Int,
    totalSteps: Int,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "✕",
                color = NureongiColors.TextPrimary,
                modifier = Modifier
                    .clickable(
                        onClickLabel = "안내 종료",
                        role = Role.Button,
                        onClick = onCloseClick,
                    )
                    .padding(8.dp),
            )
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "목적지",
                    style = NureongiTypography.ItemDescription,
                    color = NureongiColors.TextSecondary,
                )
                Text(
                    text = destinationName,
                    style = NureongiTypography.ItemTitle,
                    color = NureongiColors.TextPrimary,
                    modifier = Modifier.semantics { heading() },
                )
            }
            Text(
                text = "$currentStep / $totalSteps",
                style = NureongiTypography.ItemDescription,
                color = NureongiColors.TextSecondary,
            )
        }
        SegmentedProgressIndicator(
            totalSteps = totalSteps,
            completedSteps = currentStep,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
fun BackNavigationTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                .clickable(
                    onClickLabel = "뒤로 가기",
                    role = Role.Button,
                    onClick = onBackClick,
                )
                .padding(end = 8.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = "‹ 뒤로",
                style = NureongiTypography.ItemTitle,
                color = NureongiColors.Accent,
            )
        }
        Text(
            text = title,
            style = NureongiTypography.ScreenTitle,
            color = NureongiColors.TextPrimary,
            modifier = Modifier.semantics { heading() },
        )
    }
}

@Preview
@Composable
private fun NavigationTopBarPreview() {
    NureongiTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(NureongiColors.Background)
                .padding(20.dp),
        ) {
            NavigationTopBar(
                destinationName = "2번 출구",
                currentStep = 1,
                totalSteps = 3,
                onCloseClick = {},
            )
        }
    }
}

@Preview
@Composable
private fun BackNavigationTopBarPreview() {
    NureongiTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(NureongiColors.Background)
                .padding(20.dp),
        ) {
            BackNavigationTopBar(title = "현재 위치", onBackClick = {})
        }
    }
}
