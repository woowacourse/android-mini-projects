package a4.dogsignal.ui.connection.composable

import a4.dogsignal.ui.connection.ConnectionStepState
import a4.dogsignal.ui.connection.ConnectionStepStatus
import a4.dogsignal.ui.connection.DeviceConnectionColors
import a4.dogsignal.ui.theme.AppTheme
import a4.dogsignal.ui.theme.Divider
import a4.dogsignal.ui.theme.TextPrimary
import a4.dogsignal.ui.theme.TextTertiary
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import dognal.shared.generated.resources.Res
import dognal.shared.generated.resources.check
import dognal.shared.generated.resources.exclamation
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun ConnectionStepList(steps: List<ConnectionStepState>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "연결 전 체크",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(11.dp))

        steps.forEach { step ->
            ConnectionStep(step)
        }
    }
}

@Composable
private fun ConnectionStep(step: ConnectionStepState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, Divider, RoundedCornerShape(18.dp))
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StepStatusIcon(step.status)
        Text(
            text = step.label,
            color = TextTertiary,
            style = MaterialTheme.typography.titleSmall,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 20.sp,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun StepStatusIcon(status: ConnectionStepStatus) {
    val background = when (status) {
        ConnectionStepStatus.Done -> DeviceConnectionColors.SuccessBackground
        ConnectionStepStatus.Waiting -> DeviceConnectionColors.WaitingBackground
    }
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        when (status) {
            ConnectionStepStatus.Done -> Image(
                painter = painterResource(Res.drawable.check),
                contentDescription = null,
                modifier = Modifier.size(14.dp),
            )

            ConnectionStepStatus.Waiting -> Image(
                painter = painterResource(Res.drawable.exclamation),
                contentDescription = null,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ConnectionStepListPreview() {
    AppTheme {
        ConnectionStepList(
            steps = listOf(
                ConnectionStepState("패드 아래 센서판이 평평한가요?", ConnectionStepStatus.Done),
                ConnectionStepState("패드 초기 무게를 자동 보정할게요", ConnectionStepStatus.Done),
                ConnectionStepState("부저는 무음 모드로 시작해요", ConnectionStepStatus.Waiting),
            ),
        )
    }
}
