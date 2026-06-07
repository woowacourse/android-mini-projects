package a4.dogsignal.ui.connection

import a4.dogsignal.ui.connection.composable.ConnectButton
import a4.dogsignal.ui.connection.composable.ConnectionHeader
import a4.dogsignal.ui.connection.composable.ConnectionStepList
import a4.dogsignal.ui.connection.composable.DeviceCard
import a4.dogsignal.ui.theme.AppTheme
import a4.dogsignal.ui.theme.RecordScreenBackground
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
internal fun DeviceConnectionScreen(
    state: DeviceConnectionUiState,
    onConnectClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RecordScreenBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 31.dp),
    ) {
        Spacer(Modifier.height(28.dp))
        ConnectionHeader(state.title, state.description)
        Spacer(Modifier.height(32.dp))
        DeviceCard(state.deviceCard)
        Spacer(Modifier.height(28.dp))
        ConnectionStepList(state.steps)
        Spacer(Modifier.weight(1f))
        ConnectButton(
            label = state.actionLabel,
            onClick = onConnectClick,
            modifier = Modifier.padding(bottom = 30.dp),
        )
    }
}

@Preview
@Composable
private fun DeviceConnectionScreenPreview() {
    AppTheme {
        DeviceConnectionScreen(
            state = DeviceConnectionUiState(
                title = "기기연결",
                description = "센서 키트를 앱과 연결해요",
                deviceCard = DeviceCardState(
                    title = "Arduino 키트 연결",
                    description = "로드셀 · 초음파",
                ),
                steps = listOf(
                    ConnectionStepState("패드 아래 센서판이 평평한가요?", ConnectionStepStatus.Done),
                    ConnectionStepState("패드 초기 무게를 자동 보정할게요", ConnectionStepStatus.Done),
                    ConnectionStepState("부저는 무음 모드로 시작해요", ConnectionStepStatus.Waiting),
                ),
                actionLabel = "기기 연결하기",
            )
        )
    }
}
