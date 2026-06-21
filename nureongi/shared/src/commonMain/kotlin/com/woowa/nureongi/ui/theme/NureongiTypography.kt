package com.woowa.nureongi.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

internal val LocalNureongiFontFamily = compositionLocalOf<FontFamily> { FontFamily.Default }

object NureongiTypography {
    val ScreenTitle: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = screenTitle.withFontFamily(LocalNureongiFontFamily.current)

    val SectionHeader: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = sectionHeader.withFontFamily(LocalNureongiFontFamily.current)

    val ItemTitle: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = itemTitle.withFontFamily(LocalNureongiFontFamily.current)

    val ItemDescription: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = itemDescription.withFontFamily(LocalNureongiFontFamily.current)

    val GuidanceMessageStyle: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = guidanceMessageStyle.withFontFamily(LocalNureongiFontFamily.current)

    val GuidanceInstructionStyle: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = guidanceInstructionStyle.withFontFamily(LocalNureongiFontFamily.current)

    val StatValueStyle: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = statValueStyle.withFontFamily(LocalNureongiFontFamily.current)

    val StatLabelStyle: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = statLabelStyle.withFontFamily(LocalNureongiFontFamily.current)
}

private fun TextStyle.withFontFamily(fontFamily: FontFamily): TextStyle = copy(
    fontFamily = fontFamily,
)

private val screenTitle = TextStyle(
    fontSize = 28.sp,
    lineHeight = 34.sp,
    fontWeight = FontWeight.ExtraBold,
)

private val sectionHeader = TextStyle(
    fontSize = 18.sp,
    fontWeight = FontWeight.Bold,
)

private val itemTitle = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.SemiBold,
)

private val itemDescription = TextStyle(
    fontSize = 13.sp,
    fontWeight = FontWeight.Normal,
)

private val guidanceMessageStyle = TextStyle(
    fontSize = 24.sp,
    lineHeight = 34.sp,
    fontWeight = FontWeight.Bold,
)

private val guidanceInstructionStyle = TextStyle(
    fontSize = 36.sp,
    lineHeight = 40.sp,
    fontWeight = FontWeight.ExtraBold,
)

private val statValueStyle = TextStyle(
    fontSize = 32.sp,
    lineHeight = 36.sp,
    fontWeight = FontWeight.ExtraBold,
)

private val statLabelStyle = TextStyle(
    fontSize = 16.sp,
    lineHeight = 20.sp,
    fontWeight = FontWeight.Bold,
)
