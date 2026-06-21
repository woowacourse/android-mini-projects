package com.woowa.nureongi.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.woowa.nureongi.ui.theme.NureongiColors
import com.woowa.nureongi.ui.theme.NureongiTheme

private const val PADDING_RATIO = 0.22f
private const val DOT_RADIUS_RATIO = 0.22f

@Composable
fun BrailleIcon(
    modifier: Modifier = Modifier,
    dotColor: Color = NureongiColors.Accent,
    backgroundColor: Color = NureongiColors.Surface,
    rows: Int = 3,
    columns: Int = 3,
) {
    Canvas(
        modifier = modifier
            .clearAndSetSemantics {}
            .background(color = backgroundColor, shape = RoundedCornerShape(12.dp)),
    ) {
        val horizontalPadding = this.size.width * PADDING_RATIO
        val verticalPadding = this.size.height * PADDING_RATIO
        val drawableWidth = this.size.width - horizontalPadding * 2
        val drawableHeight = this.size.height - verticalPadding * 2
        val dotRadius = (minOf(drawableWidth / columns, drawableHeight / rows)) * DOT_RADIUS_RATIO

        for (row in 0 until rows) {
            for (column in 0 until columns) {
                val x = horizontalPadding + drawableWidth * (column + 0.5f) / columns
                val y = verticalPadding + drawableHeight * (row + 0.5f) / rows
                drawCircle(color = dotColor, radius = dotRadius, center = Offset(x, y))
            }
        }
    }
}

@Preview
@Composable
private fun BrailleIconPreview() {
    NureongiTheme {
        Row(
            modifier = Modifier
                .background(NureongiColors.Background)
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BrailleIcon()
            BrailleIcon(dotColor = NureongiColors.OnAccent, backgroundColor = NureongiColors.Accent)
        }
    }
}
