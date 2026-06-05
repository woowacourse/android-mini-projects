package a4.dogsignal.ui.connection.composable

import a4.dogsignal.theme.Divider
import a4.dogsignal.ui.connection.ConnectionStepState
import a4.dogsignal.ui.connection.ConnectionStepStatus
import a4.dogsignal.ui.connection.DeviceConnectionColors
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dognal.shared.generated.resources.Res
import dognal.shared.generated.resources.check
import dognal.shared.generated.resources.exclamation
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun ConnectionStepList(steps: List<ConnectionStepState>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
            color = DeviceConnectionColors.TextStep,
            style = MaterialTheme.typography.titleSmall,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
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
