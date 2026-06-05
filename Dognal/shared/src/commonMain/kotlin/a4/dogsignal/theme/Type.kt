package a4.dogsignal.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
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
internal fun appTypography(): Typography {
    val pretendard = pretendardFontFamily()

    return Typography(
        headlineLarge = TextStyle(
            fontFamily = pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
        ),
        headlineMedium = TextStyle(
            fontFamily = pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 34.sp,
        ),
        headlineSmall = TextStyle(
            fontFamily = pretendard,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
        ),
        titleLarge = TextStyle(
            fontFamily = pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            lineHeight = 25.sp,
        ),
        titleMedium = TextStyle(
            fontFamily = pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 23.sp,
        ),
        titleSmall = TextStyle(
            fontFamily = pretendard,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            lineHeight = 20.sp,
        ),
        bodyLarge = TextStyle(
            fontFamily = pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = pretendard,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 21.sp,
        ),
        bodySmall = TextStyle(
            fontFamily = pretendard,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            lineHeight = 18.sp,
        ),
        labelLarge = TextStyle(
            fontFamily = pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 18.sp,
        ),
        labelMedium = TextStyle(
            fontFamily = pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            lineHeight = 16.sp,
        ),
        labelSmall = TextStyle(
            fontFamily = pretendard,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
        ),
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
