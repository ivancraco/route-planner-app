package com.routeplanner.app.core.ui

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Immutable
data class Dimens(

    // Spacing
    val spaceXxs: Dp,
    val spaceXs: Dp,
    val spaceSm: Dp,
    val spaceMd: Dp,
    val spaceLg: Dp,
    val spaceXl: Dp,
    val spaceXxl: Dp,
    val space3xl: Dp,

    // Component sizes
    val buttonHeight: Dp,
    val buttonHeightSm: Dp,
    val iconSizeSm: Dp,
    val iconSizeMd: Dp,
    val iconSizeLg: Dp,
    val inputHeight: Dp,
    val appBarHeight: Dp,
    val bottomNavHeight: Dp,
    val fabSize: Dp,
    val cardElevation: Dp,

    // Border radius
    val radiusXs: Dp,
    val radiusSm: Dp,
    val radiusMd: Dp,
    val radiusLg: Dp,
    val radiusFull: Dp,

    // Layout
    val contentPaddingHorizontal: Dp,
    val contentPaddingVertical: Dp,
    val gridColumns: Int,
    val maxContentWidth: Dp,

    // Typography sizes
    val textXs: TextUnit,
    val textSm: TextUnit,
    val textMd: TextUnit,
    val textLg: TextUnit,
    val textXl: TextUnit,
    val textXxl: TextUnit,
    val textDisplay: TextUnit,
)

// Mobile (Compact)
val DefaultDimens = Dimens(

    // Spacing
    spaceXxs = 2.dp,
    spaceXs = 4.dp,
    spaceSm = 8.dp,
    spaceMd = 16.dp,
    spaceLg = 24.dp,
    spaceXl = 32.dp,
    spaceXxl = 48.dp,
    space3xl = 64.dp,

    // Component sizes
    buttonHeight = 46.dp,
    buttonHeightSm = 36.dp,
    iconSizeSm = 16.dp,
    iconSizeMd = 24.dp,
    iconSizeLg = 32.dp,
    inputHeight = 52.dp,
    appBarHeight = 56.dp,
    bottomNavHeight = 64.dp,
    fabSize = 56.dp,
    cardElevation = 2.dp,

    // Border radius
    radiusXs = 4.dp,
    radiusSm = 8.dp,
    radiusMd = 12.dp,
    radiusLg = 16.dp,
    radiusFull = 999.dp,

    // Layout
    contentPaddingHorizontal = 16.dp,
    contentPaddingVertical = 16.dp,
    gridColumns = 4,
    maxContentWidth = 600.dp,

    // Typography sizes
    textXs = 10.sp,
    textSm = 12.sp,
    textMd = 14.sp,
    textLg = 16.sp,
    textXl = 20.sp,
    textXxl = 24.sp,
    textDisplay = 32.sp,
)

// Tablet (Medium / Expanded)
val TabletDimens = Dimens(

    // Spacing
    spaceXxs = 4.dp,
    spaceXs = 8.dp,
    spaceSm = 12.dp,
    spaceMd = 20.dp,
    spaceLg = 32.dp,
    spaceXl = 48.dp,
    spaceXxl = 64.dp,
    space3xl = 96.dp,

    // Component sizes
    buttonHeight = 52.dp,
    buttonHeightSm = 40.dp,
    iconSizeSm = 20.dp,
    iconSizeMd = 28.dp,
    iconSizeLg = 40.dp,
    inputHeight = 56.dp,
    appBarHeight = 64.dp,
    bottomNavHeight = 72.dp,
    fabSize = 64.dp,
    cardElevation = 3.dp,

    // Border radius
    radiusXs = 6.dp,
    radiusSm = 10.dp,
    radiusMd = 16.dp,
    radiusLg = 24.dp,
    radiusFull = 999.dp,

    // Layout
    contentPaddingHorizontal = 32.dp,
    contentPaddingVertical = 24.dp,
    gridColumns = 8,
    maxContentWidth = 1024.dp,

    // Typography sizes
    textXs = 12.sp,
    textSm = 14.sp,
    textMd = 16.sp,
    textLg = 18.sp,
    textXl = 24.sp,
    textXxl = 30.sp,
    textDisplay = 40.sp,
)