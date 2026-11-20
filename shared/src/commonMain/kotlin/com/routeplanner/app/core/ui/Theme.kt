package com.routeplanner.app.core.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    background = PrimaryDark,
    primaryContainer = PrimaryContainerDark,
    secondary = Secondary,
    onSecondary = OnSecondary,
    error = Error,
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    background = PrimaryLight,
    primaryContainer = PrimaryContainerLight,
    secondary = Secondary,
    onSecondary = OnSecondary,
    error = Error,
)

private val LocalDimens = staticCompositionLocalOf { DefaultDimens }

@Composable
fun DimensionsProvider(
    dimens: Dimens,
    content: @Composable () -> Unit,
) {
    val dimensionSet = remember { dimens }
    CompositionLocalProvider(LocalDimens provides dimensionSet, content = content)
}

@Composable
fun RoutePlannerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val windowSize = rememberWindowSize()
    val dimens = if (windowSize == WindowSize.Compact) DefaultDimens else TabletDimens
    val typography = rememberAppTypography(dimens)
    DimensionsProvider(dimens = dimens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content,
        )
    }
}

enum class WindowSize { Compact, Medium, Expanded }

@Composable
fun rememberWindowSize(): WindowSize {
    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    val widthDp = with(density) { windowInfo.containerSize.width.toDp() }
    return when {
        widthDp < 600.dp -> WindowSize.Compact
        widthDp < 840.dp -> WindowSize.Medium
        else -> WindowSize.Expanded
    }
}

/**
 * Punto de acceso a tokens del tema desde cualquier @Composable.
 *
 * Uso:
 *   val spacing = RoutePlannerTheme.dimens.spaceMd
 *   val color = RoutePlannerTheme.colors.primary // alias de MaterialTheme.colorScheme
 */
object RoutePlannerTheme {
    val dimens: Dimens
        @Composable
        @ReadOnlyComposable
        get() = LocalDimens.current

    val colors
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme

    val typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography
}

// ─── Paleta de UI (tokens semánticos) ──────────────────────────

@Immutable
internal class UiPalette(
    val background: Color,
    val surface: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accent: Color,
    val onAccent: Color,
    val accentSoft: Color,
    val success: Color,
    val warning: Color,
    val danger: Color,
)

internal val LightPalette = UiPalette(
    background = LightBackground,
    surface = LightSurface,
    border = LightBorder,
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    accent = LightAccent,
    onAccent = LightOnAccent,
    accentSoft = LightAccentSoft,
    success = LightSuccess,
    warning = LightWarning,
    danger = LightDanger,
)

internal val DarkPalette = UiPalette(
    background = DarkBackground,
    surface = DarkSurface,
    border = DarkBorder,
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    accent = DarkAccent,
    onAccent = DarkOnAccent,
    accentSoft = DarkAccentSoft,
    success = DarkSuccess,
    warning = DarkWarning,
    danger = DarkDanger,
)

@Composable
internal fun rememberPalette(): UiPalette {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    return if (isDark) DarkPalette else LightPalette
}