package com.example.whatsinmyfridge.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun FridgeTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (useDarkTheme) FridgeDarkColors else FridgeLightColors,
        typography = FridgeTypography,
        shapes = FridgeShapes,
        content = content,
    )
}
