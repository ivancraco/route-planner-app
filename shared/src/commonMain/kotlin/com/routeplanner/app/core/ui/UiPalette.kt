package com.routeplanner.app.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

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
    background = Color(0xFFFAFAFA),
    surface = Color(0xFFFFFFFF),
    border = Color(0xFFE8EAED),
    textPrimary = Color(0xFF14161A),
    textSecondary = Color(0xFF6B7280),
    accent = Color(0xFF2F5BEA),
    onAccent = Color(0xFFFFFFFF),
    accentSoft = Color(0xFFEAF0FF),
    success = Color(0xFF1E9E6A),
    warning = Color(0xFFC27C0E),
    danger = Color(0xFFD64545),
)

internal val DarkPalette = UiPalette(
    background = Color(0xFF0F1115),
    surface = Color(0xFF171A20),
    border = Color(0xFF242830),
    textPrimary = Color(0xFFECEEF1),
    textSecondary = Color(0xFF9AA1AD),
    accent = Color(0xFF7C9BFF),
    onAccent = Color(0xFF0F1115),
    accentSoft = Color(0xFF1B2440),
    success = Color(0xFF4CC38F),
    warning = Color(0xFFE0A94A),
    danger = Color(0xFFF07178),
)

@Composable
internal fun rememberPalette(): UiPalette {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    return if (isDark) DarkPalette else LightPalette
}