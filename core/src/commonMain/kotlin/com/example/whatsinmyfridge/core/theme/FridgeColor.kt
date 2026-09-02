package com.example.whatsinmyfridge.core.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * "Frisches Bistro" - eine analoge, kühle Grün-Familie (Teal -> Blattgrün) statt
 * konkurrierender Warm-/Kalt-Akzente. Amber bleibt als einziger, bewusst warmer
 * Gegenpol für Tertiär (z.B. Warnhinweise) - alles andere bleibt in derselben
 * frischen Grün-Tonalität und wirkt dadurch wie EINE Palette statt drei zufälligen.
 */
internal val FridgeLightColors = lightColorScheme(
    primary = Color(0xFF0F7A68),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFB6F1DF),
    onPrimaryContainer = Color(0xFF00201A),
    secondary = Color(0xFF3E7D44),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFC2F0C2),
    onSecondaryContainer = Color(0xFF002106),
    tertiary = Color(0xFF9C6B00),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDEA6),
    onTertiaryContainer = Color(0xFF2B1800),
    background = Color(0xFFF4FAF7),
    onBackground = Color(0xFF151E1A),
    surface = Color(0xFFF4FAF7),
    onSurface = Color(0xFF151E1A),
    surfaceVariant = Color(0xFFDCE5DE),
    onSurfaceVariant = Color(0xFF404944),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFEDF5F0),
    surfaceContainer = Color(0xFFE6EFE9),
    surfaceContainerHigh = Color(0xFFDFE9E2),
    surfaceContainerHighest = Color(0xFFD8E2DB),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    outline = Color(0xFF6F7973),
    outlineVariant = Color(0xFFBFC9C2),
)

/** "Nachtbistro" - dieselbe kühle Grün-Familie, für die Nacht invertiert. */
internal val FridgeDarkColors = darkColorScheme(
    primary = Color(0xFF6FDBC0),
    onPrimary = Color(0xFF00382E),
    primaryContainer = Color(0xFF005343),
    onPrimaryContainer = Color(0xFF8BF8DC),
    secondary = Color(0xFFA0D6A2),
    onSecondary = Color(0xFF0B3913),
    secondaryContainer = Color(0xFF234F29),
    onSecondaryContainer = Color(0xFFBCF0BC),
    tertiary = Color(0xFFF0C060),
    onTertiary = Color(0xFF422D00),
    tertiaryContainer = Color(0xFF614300),
    onTertiaryContainer = Color(0xFFFFDEA6),
    background = Color(0xFF0E1512),
    onBackground = Color(0xFFDEE4E0),
    surface = Color(0xFF0E1512),
    onSurface = Color(0xFFDEE4E0),
    surfaceVariant = Color(0xFF404944),
    onSurfaceVariant = Color(0xFFC0C9C2),
    surfaceContainerLowest = Color(0xFF090E0C),
    surfaceContainerLow = Color(0xFF161D1A),
    surfaceContainer = Color(0xFF1A2220),
    surfaceContainerHigh = Color(0xFF252D2A),
    surfaceContainerHighest = Color(0xFF303834),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    outline = Color(0xFF8A9891),
    outlineVariant = Color(0xFF404944),
)

/**
 * "Verspielt" - der Gegenentwurf zur kühlen Bistro-Familie: eine durchgehend warme,
 * analoge "Obststand"-Palette (Mandarine -> Himbeere -> Sonnenblume). Alle drei
 * Akzente liegen im selben warmen Farbraum, statt sich gegenseitig zu stören -
 * fühlt sich dadurch bewusst laut, aber trotzdem stimmig an.
 */
internal val FridgePlayfulColors = lightColorScheme(
    primary = Color(0xFFE8590C),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDBC4),
    onPrimaryContainer = Color(0xFF331100),
    secondary = Color(0xFFE0446B),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFD9E1),
    onSecondaryContainer = Color(0xFF3F0011),
    tertiary = Color(0xFFE0B400),
    onTertiary = Color(0xFF3A2E00),
    tertiaryContainer = Color(0xFFFFE897),
    onTertiaryContainer = Color(0xFF241A00),
    background = Color(0xFFFFF8F1),
    onBackground = Color(0xFF2A1C10),
    surface = Color(0xFFFFF8F1),
    onSurface = Color(0xFF2A1C10),
    surfaceVariant = Color(0xFFF1DFC9),
    onSurfaceVariant = Color(0xFF52443A),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFFF1E2),
    surfaceContainer = Color(0xFFFCE9D3),
    surfaceContainerHigh = Color(0xFFF6E1C6),
    surfaceContainerHighest = Color(0xFFF0D9B9),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    outline = Color(0xFF8A7761),
    outlineVariant = Color(0xFFDBC7AA),
)

/** Kleiner, kontextfreier Farbausschnitt eines Themes - für Vorschau-Kacheln in der Theme-Auswahl. */
data class ThemeSwatch(
    val background: Color,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
)

internal fun ColorScheme.toSwatch() = ThemeSwatch(
    background = background,
    primary = primary,
    secondary = secondary,
    tertiary = tertiary,
)
