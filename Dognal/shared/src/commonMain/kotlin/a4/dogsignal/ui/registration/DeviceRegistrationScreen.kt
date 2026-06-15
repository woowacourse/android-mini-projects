package a4.dogsignal.ui.registration

import a4.dogsignal.ui.common.component.ScreenHeader
import a4.dogsignal.ui.registration.composable.DeviceRegistrationButton
import a4.dogsignal.ui.registration.composable.DeviceRegistrationCard
import a4.dogsignal.ui.registration.composable.DeviceRegistrationStepList
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
internal fun DeviceRegistrationScreen(
    state: DeviceRegistrationUiState,
    onRegisterClick: () -> Unit = {},
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(RecordScreenBackground)
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 31.dp),
    ) {
        Spacer(Modifier.height(28.dp))
        ScreenHeader(
            title = "디바이스 등록",
            subtitle = "센서 키트를 앱과 연결해요",
        )
        Spacer(Modifier.height(32.dp))
        DeviceRegistrationCard(state.deviceCard)
        Spacer(Modifier.height(28.dp))
        DeviceRegistrationStepList(state.steps)
        Spacer(Modifier.weight(1f))
        DeviceRegistrationButton(
            label = state.actionLabel,
            onClick = onRegisterClick,
            modifier = Modifier.padding(bottom = 30.dp),
        )
    }
}

@Preview
@Composable
private fun DeviceRegistrationScreenPreview() {
    AppTheme {
        DeviceRegistrationScreen(
            state =
                DeviceRegistrationUiState(
                    deviceCard =
                        DeviceRegistrationCardState(
                            title = "Arduino 키트 등록",
                            description = "로드셀 · 초음파",
                        ),
                    steps =
                        listOf(
                            DeviceRegistrationStepState("패드 아래 센서판이 평평한가요?", DeviceRegistrationStepStatus.Done),
                            DeviceRegistrationStepState("패드 초기 무게를 자동 보정할게요", DeviceRegistrationStepStatus.Done),
                            DeviceRegistrationStepState("부저는 무음 모드로 시작해요", DeviceRegistrationStepStatus.Waiting),
                        ),
                    actionLabel = "기기 등록하기",
                ),
        )
    }
}
