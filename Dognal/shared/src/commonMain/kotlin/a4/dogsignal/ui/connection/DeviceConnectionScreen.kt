package a4.dogsignal.ui.connection

import a4.dogsignal.ui.theme.DognalColors
import a4.dogsignal.ui.theme.dognalTypography
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dognal.shared.generated.resources.Res
import dognal.shared.generated.resources.arduino
import dognal.shared.generated.resources.check
import dognal.shared.generated.resources.exclamation
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun DeviceConnectionScreen(
    state: DeviceConnectionUiState = DeviceConnectionUiState.preview(),
    onConnectClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeviceConnectionColors.Background)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 31.dp),
    ) {
        Spacer(Modifier.height(28.dp))
        Header(state.title, state.description)
        Spacer(Modifier.height(32.dp))
        DeviceCard(state.deviceCard)
        Spacer(Modifier.height(28.dp))
        StepList(state.steps)
        Spacer(Modifier.weight(1f))
        ConnectButton(
            label = state.actionLabel,
            onClick = onConnectClick,
            modifier = Modifier.padding(bottom = 30.dp),
        )
    }
}

@Composable
private fun Header(
    title: String,
    description: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = title,
            color = DognalColors.TextPrimary,
            style = MaterialTheme.typography.headlineMedium,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 34.sp,
        )
        Text(
            text = description,
            color = DognalColors.TextSecondary,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 14.sp,
            lineHeight = 21.sp,
        )
    }
}

@Composable
private fun DeviceCard(state: DeviceCardState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(132.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(DognalColors.WarmBackground)
            .border(
                width = 1.dp,
                color = DognalColors.WarmOutline,
                shape = RoundedCornerShape(28.dp),
            )
            .padding(horizontal = 28.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Image(
            painter = painterResource(Res.drawable.arduino),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
        )
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = state.title,
                color = DognalColors.TextPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 25.sp,
            )
            Text(
                text = state.description,
                color = DognalColors.TextSecondary,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 13.sp,
                lineHeight = 19.sp,
            )
        }
    }
}

@Composable
private fun StepList(steps: List<ConnectionStepState>) {
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
            .border(1.dp, DognalColors.Outline, RoundedCornerShape(18.dp))
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StatusIcon(step.status)
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
private fun StatusIcon(status: ConnectionStepStatus) {
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

@Composable
private fun ConnectButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DognalColors.Primary,
            contentColor = Color.White,
        ),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
private fun DeviceConnectionScreenPreview() {
    MaterialTheme(
        typography = dognalTypography(),
    ) {
        DeviceConnectionScreen()
    }
}
