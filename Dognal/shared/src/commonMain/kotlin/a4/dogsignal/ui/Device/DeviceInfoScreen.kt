package a4.dogsignal.ui.Device

import a4.dogsignal.ui.Device.composable.DeviceInfoHeader
import a4.dogsignal.ui.Device.composable.UserInfoPrimaryButton
import a4.dogsignal.ui.Device.composable.UserInfoTextField
import a4.dogsignal.ui.theme.AppTheme
import a4.dogsignal.ui.theme.HomeBackground
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
internal fun DeviceInfoScreen(
    state: DeviceInfoUiState,
    onUserIdChange: (String) -> Unit,
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
        DeviceInfoHeader()
        Spacer(Modifier.height(68.dp))
        UserInfoTextField(
            label = "사용자 ID",
            value = state.id,
            placeholder = "예) 58b44b40-f507-4516-88dd-ec8c8...",
            onValueChange = onUserIdChange,
        )
        Spacer(Modifier.weight(1f))
        UserInfoPrimaryButton(
            text = "입력 완료",
            onClick = onSubmitClick,
            modifier = Modifier.padding(bottom = 48.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DeviceInfoScreenPreview() {
    AppTheme {
        DeviceInfoScreen(
            state = DeviceInfoUiState(id = "58b44b40-f507-4516-88dd-ec8c8"),
            onUserIdChange = {},
            onSubmitClick = {},
        )
    }
}
