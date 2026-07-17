package com.rain.dailygrind.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Immutable
data class GrindColors(
    val bgBase: Color,
    val bgSurface: Color,
    val bgGlass: Color,
    val accent: Color,
    val accentAlt: Color,
    val danger: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val divider: Color
)

val LightGrind = GrindColors(
    bgBase = Color(0xFFF6F6F8),
    bgSurface = Color(0xFFFFFFFF),
    bgGlass = Color(0xB3FFFFFF),
    accent = Color(0xFF0091AD),
    accentAlt = Color(0xFF7A4FE0),
    danger = Color(0xFFE0405C),
    textPrimary = Color(0xFF14141A),
    textSecondary = Color(0xFF6B6B78),
    divider = Color(0x14000000)
)

val DarkGrind = GrindColors(
    bgBase = Color(0xFF0B0B10),
    bgSurface = Color(0xFF15151C),
    bgGlass = Color(0x8C15151C),
    accent = Color(0xFF6FE3FF),
    accentAlt = Color(0xFFB98CFF),
    danger = Color(0xFFFF5C7A),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFF9A9AA8),
    divider = Color(0x14FFFFFF)
)

val LocalGrindColors = staticCompositionLocalOf { LightGrind }

val GrindTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp
    )
)

@Composable
fun DailyGrindTheme(
    dark: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val target = if (dark) DarkGrind else LightGrind
    val bgBase by animateColorAsState(target.bgBase, tween(300), label = "bg")
    val bgSurface by animateColorAsState(target.bgSurface, tween(300), label = "surface")
    val accent by animateColorAsState(target.accent, tween(300), label = "accent")
    val accentAlt by animateColorAsState(target.accentAlt, tween(300), label = "accentAlt")
    val textPrimary by animateColorAsState(target.textPrimary, tween(300), label = "tp")
    val textSecondary by animateColorAsState(target.textSecondary, tween(300), label = "ts")
    val colors = target.copy(
        bgBase = bgBase,
        bgSurface = bgSurface,
        accent = accent,
        accentAlt = accentAlt,
        textPrimary = textPrimary,
        textSecondary = textSecondary
    )
    val scheme = if (dark) {
        darkColorScheme(
            primary = colors.accent,
            background = colors.bgBase,
            surface = colors.bgSurface,
            onPrimary = Color.White,
            onBackground = colors.textPrimary,
            onSurface = colors.textPrimary
        )
    } else {
        lightColorScheme(
            primary = colors.accent,
            background = colors.bgBase,
            surface = colors.bgSurface,
            onPrimary = Color.White,
            onBackground = colors.textPrimary,
            onSurface = colors.textPrimary
        )
    }
    CompositionLocalProvider(LocalGrindColors provides colors) {
        MaterialTheme(colorScheme = scheme, typography = GrindTypography, content = content)
    }
}

object GrindTheme {
    val colors: GrindColors
        @Composable get() = LocalGrindColors.current
}
