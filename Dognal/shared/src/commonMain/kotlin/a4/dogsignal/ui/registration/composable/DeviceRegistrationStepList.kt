package a4.dogsignal.ui.registration.composable

import a4.dogsignal.ui.registration.DeviceRegistrationStepState
import a4.dogsignal.ui.registration.DeviceRegistrationStepStatus
import a4.dogsignal.ui.theme.AppTheme
import a4.dogsignal.ui.theme.DeviceSuccessSurface
import a4.dogsignal.ui.theme.DeviceWaitingSurface
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
internal fun DeviceRegistrationStepList(steps: List<DeviceRegistrationStepState>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "등록 전 체크",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 11.dp),
        )

        steps.forEach { step ->
            DeviceRegistrationStep(step)
        }
    }
}

@Composable
private fun DeviceRegistrationStep(step: DeviceRegistrationStepState) {
    Row(
        modifier =
            Modifier
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
private fun StepStatusIcon(status: DeviceRegistrationStepStatus) {
    val background =
        when (status) {
            DeviceRegistrationStepStatus.Done -> DeviceSuccessSurface
            DeviceRegistrationStepStatus.Waiting -> DeviceWaitingSurface
        }
    Box(
        modifier =
            Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(background),
        contentAlignment = Alignment.Center,
    ) {
        when (status) {
            DeviceRegistrationStepStatus.Done ->
                Image(
                    painter = painterResource(Res.drawable.check),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                )

            DeviceRegistrationStepStatus.Waiting ->
                Image(
                    painter = painterResource(Res.drawable.exclamation),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DeviceRegistrationStepListPreview() {
    AppTheme {
        DeviceRegistrationStepList(
            steps =
                listOf(
                    DeviceRegistrationStepState("패드 아래 센서판이 평평한가요?", DeviceRegistrationStepStatus.Done),
                    DeviceRegistrationStepState("패드 초기 무게를 자동 보정할게요", DeviceRegistrationStepStatus.Done),
                ),
        )
    }
}
