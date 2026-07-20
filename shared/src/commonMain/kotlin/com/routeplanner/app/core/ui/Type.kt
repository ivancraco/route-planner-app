package com.routeplanner.app.core.ui

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import route_planner_app.shared.generated.resources.Res
import route_planner_app.shared.generated.resources.lato_black
import route_planner_app.shared.generated.resources.lato_bold
import route_planner_app.shared.generated.resources.lato_light
import route_planner_app.shared.generated.resources.lato_regular

@Composable
fun rememberAppTypography(dimens: Dimens): Typography {
    val fontFamily = FontFamily(
        Font(Res.font.lato_light, FontWeight.Light),
        Font(Res.font.lato_regular, FontWeight.Normal),
        Font(Res.font.lato_bold, FontWeight.Bold),
        Font(Res.font.lato_black, FontWeight.ExtraBold),
    )
    return Typography(
        displayLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = dimens.textDisplay
        ),
        headlineLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = dimens.textXxl
        ),
        headlineMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = dimens.textXl
        ),
        titleLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = dimens.textLg
        ),
        titleMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = dimens.textMd
        ),
        titleSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = dimens.textSm
        ),
        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = dimens.textLg
        ),
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = dimens.textMd
        ),
        bodySmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = dimens.textSm
        ),
        labelLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = dimens.textMd
        ),
        labelMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = dimens.textSm
        ),
        labelSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = dimens.textXs
        )
    )
}