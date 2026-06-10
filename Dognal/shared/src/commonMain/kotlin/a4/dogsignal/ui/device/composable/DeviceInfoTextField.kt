package a4.dogsignal.ui.device.composable

import a4.dogsignal.ui.theme.AppTheme
import a4.dogsignal.ui.theme.Divider
import a4.dogsignal.ui.theme.TextDisabled
import a4.dogsignal.ui.theme.TextPrimary
import a4.dogsignal.ui.theme.TextTertiary
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dognal.shared.generated.resources.Res
import dognal.shared.generated.resources.people
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun DeviceInfoTextField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = label,
            color = TextTertiary,
            style = MaterialTheme.typography.bodyLarge,
        )
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White)
                    .border(1.5.dp, Divider, RoundedCornerShape(18.dp))
                    .padding(horizontal = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Image(
                painter = painterResource(Res.drawable.people),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle =
                    MaterialTheme.typography.bodySmall.merge(
                        TextStyle(color = TextPrimary),
                    ),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = TextDisabled,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    innerTextField()
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DeviceInfoTextFieldPreview() {
    AppTheme {
        DeviceInfoTextField(
            label = "아이디",
            value = "romi_romi",
            placeholder = "romi_romi",
            onValueChange = {},
        )
    }
}
