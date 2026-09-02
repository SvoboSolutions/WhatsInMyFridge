package com.example.whatsinmyfridge.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import com.example.whatsinmyfridge.domain.model.ThemeMode

@Composable
fun FridgeTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    val isSystemDark = isSystemInDarkTheme()
    val (colorScheme, shapes) = resolveFridgeLook(themeMode, isSystemDark)
    MaterialTheme(
        colorScheme = colorScheme,
        typography = FridgeTypography,
        shapes = shapes,
        content = content,
    )
}

private fun resolveFridgeLook(themeMode: ThemeMode, isSystemDark: Boolean): Pair<ColorScheme, Shapes> =
    when (themeMode) {
        ThemeMode.SYSTEM -> (if (isSystemDark) FridgeDarkColors else FridgeLightColors) to FridgeShapes
        ThemeMode.LIGHT -> FridgeLightColors to FridgeShapes
        ThemeMode.DARK -> FridgeDarkColors to FridgeShapes
        ThemeMode.PLAYFUL -> FridgePlayfulColors to FridgePlayfulShapes
        ThemeMode.SUNSET -> FridgeSunsetColors to FridgeShapes
        ThemeMode.BERRY -> FridgeBerryColors to FridgeShapes
        ThemeMode.SAGE -> FridgeSageColors to FridgeShapes
        ThemeMode.POMEGRANATE -> FridgePomegranateColors to FridgeShapes
        ThemeMode.CITRUS -> FridgeCitrusColors to FridgePlayfulShapes
        ThemeMode.MIDNIGHT -> FridgeMidnightColors to FridgeShapes
        ThemeMode.KONTRAST -> FridgeKontrastColors to FridgeSharpShapes
    }

/** Vorschau-Farben für die Theme-Auswahl - unabhängig vom aktuell aktiven Theme. */
fun themeSwatch(themeMode: ThemeMode, isSystemDark: Boolean): ThemeSwatch =
    when (themeMode) {
        ThemeMode.SYSTEM -> (if (isSystemDark) FridgeDarkColors else FridgeLightColors).toSwatch()
        ThemeMode.LIGHT -> FridgeLightColors.toSwatch()
        ThemeMode.DARK -> FridgeDarkColors.toSwatch()
        ThemeMode.PLAYFUL -> FridgePlayfulColors.toSwatch()
        ThemeMode.SUNSET -> FridgeSunsetColors.toSwatch()
        ThemeMode.BERRY -> FridgeBerryColors.toSwatch()
        ThemeMode.SAGE -> FridgeSageColors.toSwatch()
        ThemeMode.POMEGRANATE -> FridgePomegranateColors.toSwatch()
        ThemeMode.CITRUS -> FridgeCitrusColors.toSwatch()
        ThemeMode.MIDNIGHT -> FridgeMidnightColors.toSwatch()
        ThemeMode.KONTRAST -> FridgeKontrastColors.toSwatch()
    }
