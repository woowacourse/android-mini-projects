package com.woowa.nureongi.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.woowa.nureongi.ui.theme.NureongiColors
import com.woowa.nureongi.ui.theme.NureongiTheme
import com.woowa.nureongi.ui.theme.NureongiTypography
import com.woowa.nureongi.ui.theme.NureongiTypography.GuidanceInstructionStyle
import com.woowa.nureongi.ui.theme.NureongiTypography.GuidanceMessageStyle


@Composable
fun DirectionGuideCard(
    instruction: String,
    landmark: String,
    guideMessage: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(NureongiColors.Surface)
            .padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clearAndSetSemantics {
                contentDescription = "$instruction. $landmark"
                heading()
            },
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(NureongiColors.Background),
                contentAlignment = Alignment.Center,
            ) {
                leadingIcon()
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = instruction,
                    style = GuidanceInstructionStyle,
                    color = NureongiColors.Accent,
                )
                Text(
                    text = landmark,
                    style = NureongiTypography.ItemTitle,
                    color = NureongiColors.TextSecondary,
                )
            }
        }
        Text(
            text = guideMessage,
            style = GuidanceMessageStyle,
            color = NureongiColors.TextPrimary,
        )
    }
}

@Composable
fun StraightArrowIcon(
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val strokeWidth = size.minDimension * 0.14f
        val centerX = size.width / 2f
        val topY = size.height * 0.12f
        val bottomY = size.height * 0.86f
        val arrowSideY = size.height * 0.34f
        val arrowSideX = size.width * 0.22f

        drawLine(
            color = NureongiColors.Accent,
            start = Offset(centerX, bottomY),
            end = Offset(centerX, topY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = NureongiColors.Accent,
            start = Offset(centerX, topY),
            end = Offset(arrowSideX, arrowSideY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = NureongiColors.Accent,
            start = Offset(centerX, topY),
            end = Offset(size.width - arrowSideX, arrowSideY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
    }
}

@Preview
@Composable
private fun DirectionGuideCardPreview() {
    NureongiTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(NureongiColors.Background)
                .padding(20.dp),
        ) {
            DirectionGuideCard(
                instruction = "8m 직진",
                landmark = "다음 점형 블록 · 출구 갈림길",
                guideMessage = "2번 출구까지 안내를 시작합니다. 앞으로 8미터 직진하세요. 8미터 앞에 갈림길이 있습니다.",
                leadingIcon = {
                    StraightArrowIcon()
                },
            )
        }
    }
}
