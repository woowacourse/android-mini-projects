package com.woowa.nureongi.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import nureongi.shared.generated.resources.Res
import nureongi.shared.generated.resources.koddiudongothic_bold
import nureongi.shared.generated.resources.koddiudongothic_extrabold
import nureongi.shared.generated.resources.koddiudongothic_regular
import org.jetbrains.compose.resources.Font

private val nureongiColorScheme = darkColorScheme(
    background = NureongiColors.Background,
    surface = NureongiColors.Surface,
    surfaceVariant = NureongiColors.Surface,
    primary = NureongiColors.Accent,
    onPrimary = NureongiColors.OnAccent,
    onBackground = NureongiColors.TextPrimary,
    onSurface = NureongiColors.TextPrimary,
    onSurfaceVariant = NureongiColors.TextSecondary,
)

@Composable
fun NureongiTheme(content: @Composable () -> Unit) {
    val fontFamily = nureongiFontFamily()

    CompositionLocalProvider(LocalNureongiFontFamily provides fontFamily) {
        MaterialTheme(
            colorScheme = nureongiColorScheme,
            typography = nureongiMaterialTypography(),
            content = content,
        )
    }
}

@Composable
private fun nureongiFontFamily(): FontFamily = FontFamily(
    Font(Res.font.koddiudongothic_regular, FontWeight.Normal),
    Font(Res.font.koddiudongothic_bold, FontWeight.SemiBold),
    Font(Res.font.koddiudongothic_bold, FontWeight.Bold),
    Font(Res.font.koddiudongothic_extrabold, FontWeight.ExtraBold),
)

@Composable
private fun nureongiMaterialTypography() = Typography(
    titleMedium = NureongiTypography.SectionHeader,
    bodyLarge = NureongiTypography.ItemTitle,
    bodyMedium = NureongiTypography.ItemDescription,
)
