package a4.dogsignal.ui.device

import a4.dogsignal.data.repository.DeviceRepository
import a4.dogsignal.ui.device.composable.DeviceInfoHeader
import a4.dogsignal.ui.device.composable.DeviceInfoPrimaryButton
import a4.dogsignal.ui.device.composable.DeviceInfoTextField
import a4.dogsignal.ui.theme.AppTheme
import a4.dogsignal.ui.theme.HomeBackground
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
internal fun DeviceInfoScreen(
    deviceRepository: DeviceRepository,
    onAuthenticated: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: DeviceInfoViewModel = viewModel { DeviceInfoViewModel(deviceRepository) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.navigationEvent.collect(onAuthenticated)
    }

    DeviceInfoScreen(
        state = state,
        onCodeChange = viewModel::onCodeChange,
        onSecretChange = viewModel::onSecretChange,
        onSubmitClick = viewModel::onSubmit,
        modifier = modifier,
    )
}

@Composable
internal fun DeviceInfoScreen(
    state: DeviceInfoUiState,
    onCodeChange: (String) -> Unit,
    onSecretChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(HomeBackground)
                .padding(horizontal = 31.dp),
    ) {
        Spacer(Modifier.height(70.dp))
        DeviceInfoHeader(modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(68.dp))
        DeviceInfoTextField(
            label = "디바이스 코드",
            value = state.code,
            placeholder = "예) pad-001",
            onValueChange = onCodeChange,
        )
        Spacer(Modifier.height(25.dp))
        DeviceInfoTextField(
            label = "디바이스 시크릿",
            value = state.secret,
            placeholder = "예) pad-001-device...",
            onValueChange = onSecretChange,
        )
        if (state.error != null) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = state.error,
                color = Color(0xFFE53935),
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Spacer(Modifier.weight(1f))
        DeviceInfoPrimaryButton(
            text = if (state.isLoading) "확인 중..." else "입력 완료",
            onClick = onSubmitClick,
            enabled = !state.isLoading,
            modifier = Modifier.padding(bottom = 48.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DeviceInfoScreenPreview() {
    AppTheme {
        DeviceInfoScreen(
            state = DeviceInfoUiState(code = "pad-001", secret = ""),
            onCodeChange = {},
            onSecretChange = {},
            onSubmitClick = {},
        )
    }
}
