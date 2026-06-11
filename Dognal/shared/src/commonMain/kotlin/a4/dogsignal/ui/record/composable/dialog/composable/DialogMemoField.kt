package a4.dogsignal.ui.record.composable.dialog.composable

import a4.dogsignal.ui.theme.Divider
import a4.dogsignal.ui.theme.TextDisabled
import a4.dogsignal.ui.theme.TextPrimary
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
internal fun DialogMemoField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    BasicTextField(
        value = value,
        onValueChange = { newValue ->
            onValueChange(newValue.withoutLineBreak())
        },
        modifier =
            modifier
                .fillMaxWidth()
                .height(58.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFF8FAFC))
                .border(1.dp, Divider, RoundedCornerShape(20.dp))
                .padding(horizontal = 22.dp, vertical = 18.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions =
            KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                },
            ),
        textStyle =
            MaterialTheme.typography.bodySmall.merge(
                TextStyle(
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium,
                ),
            ),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = "예: 색이 진함, 횟수 잦음",
                        color = TextDisabled,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
                innerTextField()
            }
        },
    )
}

private fun String.withoutLineBreak(): String =
    takeWhile { character ->
        character != '\n' && character != '\r'
    }
