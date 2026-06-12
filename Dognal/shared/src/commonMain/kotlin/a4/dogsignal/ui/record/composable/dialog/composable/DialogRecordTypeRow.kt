package a4.dogsignal.ui.record.composable.dialog.composable

import a4.dogsignal.model.RecordType
import a4.dogsignal.ui.common.toColor
import a4.dogsignal.ui.theme.AppTheme
import a4.dogsignal.ui.theme.BrandSurface
import a4.dogsignal.ui.theme.CoolBackground
import a4.dogsignal.ui.theme.Divider
import a4.dogsignal.ui.theme.TextTertiary
import a4.dogsignal.ui.theme.WarmSurface
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dognal.shared.generated.resources.Res
import dognal.shared.generated.resources.stool
import dognal.shared.generated.resources.urine
import dognal.shared.generated.resources.visit
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private data class ManualRecordTypeOption(
    val recordType: RecordType,
    val label: String,
    val icon: DrawableResource,
    val color: Color,
    val background: Color,
)

@Composable
internal fun DialogRecordTypeRow(
    selectedRecordType: RecordType,
    onRecordTypeClick: (RecordType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        manualRecordTypeOptions().forEach { option ->
            DialogRecordTypeCard(
                option = option,
                isSelected = selectedRecordType == option.recordType,
                onClick = { onRecordTypeClick(option.recordType) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun DialogRecordTypeCard(
    option: ManualRecordTypeOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .height(90.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(if (isSelected) option.background else Color.White)
                .border(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) option.color else Divider,
                    shape = RoundedCornerShape(20.dp),
                )
                .clickable(onClick = onClick)
                .padding(top = 10.dp, bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            modifier =
                Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(option.color.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(option.icon),
                contentDescription = option.label,
                modifier = Modifier.size(24.dp),
            )
        }
        Text(
            text = option.label,
            color = if (isSelected) option.color else TextTertiary,
            style = MaterialTheme.typography.labelLarge,
            fontSize = 12.sp,
            lineHeight = 16.sp,
        )
    }
}

private fun manualRecordTypeOptions(): List<ManualRecordTypeOption> =
    listOf(
        ManualRecordTypeOption(
            recordType = RecordType.URINE,
            label = "소변",
            icon = Res.drawable.urine,
            color = RecordType.URINE.toColor(),
            background = WarmSurface,
        ),
        ManualRecordTypeOption(
            recordType = RecordType.STOOL,
            label = "대변",
            icon = Res.drawable.stool,
            color = RecordType.STOOL.toColor(),
            background = CoolBackground,
        ),
        ManualRecordTypeOption(
            recordType = RecordType.PAD,
            label = "방문",
            icon = Res.drawable.visit,
            color = RecordType.PAD.toColor(),
            background = BrandSurface,
        ),
    )

@Preview(showBackground = true)
@Composable
private fun DialogRecordTypeRowPreview() {
    AppTheme {
        DialogRecordTypeRow(
            selectedRecordType = RecordType.URINE,
            onRecordTypeClick = {},
        )
    }
}
