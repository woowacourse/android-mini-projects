package a4.dogsignal.ui.record.composable.dialog.composable

import a4.dogsignal.ui.theme.BrandPrimary
import a4.dogsignal.ui.theme.ErrorRed
import a4.dogsignal.ui.theme.TextTertiary
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun DialogActionButtons(
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
    isSaveEnabled: Boolean,
    saveText: String,
    isEditing: Boolean = false,
    onDeleteClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(26.dp),
    ) {
        if (isEditing) {
            DialogActionButton(
                text = "삭제",
                containerColor = ErrorRed.copy(alpha = 0.12f),
                contentColor = ErrorRed,
                onClick = onDeleteClick,
                modifier = Modifier.weight(1f),
            )
        } else {
            DialogActionButton(
                text = "취소",
                containerColor = Color(0xEEF0F4F7),
                contentColor = TextTertiary,
                onClick = onCancelClick,
                modifier = Modifier.weight(1f),
            )
        }
        DialogActionButton(
            text = saveText,
            containerColor = BrandPrimary,
            contentColor = Color.White,
            onClick = onSaveClick,
            enabled = isSaveEnabled,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DialogActionButton(
    text: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .height(48.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(containerColor)
                .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = contentColor,
            style = MaterialTheme.typography.labelLarge,
            fontSize = 14.sp,
            lineHeight = 18.sp,
        )
    }
}
