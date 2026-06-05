package a4.dogsignal.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import dognal.shared.generated.resources.Pretendard_Black
import dognal.shared.generated.resources.Pretendard_Bold
import dognal.shared.generated.resources.Pretendard_ExtraBold
import dognal.shared.generated.resources.Pretendard_Light
import dognal.shared.generated.resources.Pretendard_Medium
import dognal.shared.generated.resources.Pretendard_Regular
import dognal.shared.generated.resources.Pretendard_SemiBold
import dognal.shared.generated.resources.Pretendard_Thin
import dognal.shared.generated.resources.Res
import org.jetbrains.compose.resources.Font

@Composable
internal fun dognalTypography(): Typography {
    val pretendard = pretendardFontFamily()
    val default = Typography()

    return default.copy(
        displayLarge = default.displayLarge.copy(fontFamily = pretendard),
        displayMedium = default.displayMedium.copy(fontFamily = pretendard),
        displaySmall = default.displaySmall.copy(fontFamily = pretendard),
        headlineLarge = default.headlineLarge.copy(fontFamily = pretendard),
        headlineMedium = default.headlineMedium.copy(fontFamily = pretendard),
        headlineSmall = default.headlineSmall.copy(fontFamily = pretendard),
        titleLarge = default.titleLarge.copy(fontFamily = pretendard),
        titleMedium = default.titleMedium.copy(fontFamily = pretendard),
        titleSmall = default.titleSmall.copy(fontFamily = pretendard),
        bodyLarge = default.bodyLarge.copy(fontFamily = pretendard),
        bodyMedium = default.bodyMedium.copy(fontFamily = pretendard),
        bodySmall = default.bodySmall.copy(fontFamily = pretendard),
        labelLarge = default.labelLarge.copy(fontFamily = pretendard),
        labelMedium = default.labelMedium.copy(fontFamily = pretendard),
        labelSmall = default.labelSmall.copy(fontFamily = pretendard),
    )
}

@Composable
private fun pretendardFontFamily(): FontFamily = FontFamily(
    Font(Res.font.Pretendard_Thin, weight = FontWeight.Thin),
    Font(Res.font.Pretendard_Light, weight = FontWeight.Light),
    Font(Res.font.Pretendard_Regular, weight = FontWeight.Normal),
    Font(Res.font.Pretendard_Medium, weight = FontWeight.Medium),
    Font(Res.font.Pretendard_SemiBold, weight = FontWeight.SemiBold),
    Font(Res.font.Pretendard_Bold, weight = FontWeight.Bold),
    Font(Res.font.Pretendard_ExtraBold, weight = FontWeight.ExtraBold),
    Font(Res.font.Pretendard_Black, weight = FontWeight.Black),
)
