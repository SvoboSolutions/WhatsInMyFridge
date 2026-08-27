package com.example.whatsinmyfridge.core.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Warmes, appetitanregendes Food-App-Schema statt generischem Material-Grün:
 * Terracotta (Primary) + Waldgrün (Secondary) + Senfgelb (Tertiary) auf cremigem Untergrund.
 */
internal val FridgeLightColors = lightColorScheme(
    primary = Color(0xFFE8590C),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDBC7),
    onPrimaryContainer = Color(0xFF3A0900),
    secondary = Color(0xFF2D6A4F),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFC5F1D9),
    onSecondaryContainer = Color(0xFF00210F),
    tertiary = Color(0xFFC98A00),
    onTertiary = Color(0xFF3A2800),
    tertiaryContainer = Color(0xFFFFDEA0),
    onTertiaryContainer = Color(0xFF261A00),
    background = Color(0xFFFFF8F2),
    onBackground = Color(0xFF231A14),
    surface = Color(0xFFFFF8F2),
    onSurface = Color(0xFF231A14),
    surfaceVariant = Color(0xFFF0E0D3),
    onSurfaceVariant = Color(0xFF52443A),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    outline = Color(0xFF84746A),
    outlineVariant = Color(0xFFD6C4B8),
)

internal val FridgeDarkColors = darkColorScheme(
    primary = Color(0xFFFFB68C),
    onPrimary = Color(0xFF5A1A00),
    primaryContainer = Color(0xFF7C2D00),
    onPrimaryContainer = Color(0xFFFFDBC7),
    secondary = Color(0xFF8FD9B4),
    onSecondary = Color(0xFF00391F),
    secondaryContainer = Color(0xFF0F5132),
    onSecondaryContainer = Color(0xFFC5F1D9),
    tertiary = Color(0xFFEBC16E),
    onTertiary = Color(0xFF3F2D00),
    tertiaryContainer = Color(0xFF5A4200),
    onTertiaryContainer = Color(0xFFFFDEA0),
    background = Color(0xFF19140F),
    onBackground = Color(0xFFEDE0D6),
    surface = Color(0xFF19140F),
    onSurface = Color(0xFFEDE0D6),
    surfaceVariant = Color(0xFF52443A),
    onSurfaceVariant = Color(0xFFD6C4B8),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    outline = Color(0xFF9C8D80),
    outlineVariant = Color(0xFF52443A),
)
