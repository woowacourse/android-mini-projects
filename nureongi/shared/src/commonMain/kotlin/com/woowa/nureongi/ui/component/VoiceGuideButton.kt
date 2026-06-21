package com.woowa.nureongi.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.woowa.nureongi.ui.theme.NureongiColors
import com.woowa.nureongi.ui.theme.NureongiTheme
import nureongi.shared.generated.resources.Res
import nureongi.shared.generated.resources.ic_volume
import org.jetbrains.compose.resources.painterResource

@Composable
fun VoiceGuideButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(NureongiColors.Accent)
            .clickable(
                onClickLabel = "음성 안내 다시 듣기",
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_volume),
            contentDescription = null,
        )
    }
}

@Preview
@Composable
private fun VoiceGuideButtonPreview() {
    NureongiTheme {
        Box(
            modifier = Modifier
                .background(NureongiColors.Background)
                .padding(20.dp),
        ) {
            VoiceGuideButton(onClick = {})
        }
    }
}
