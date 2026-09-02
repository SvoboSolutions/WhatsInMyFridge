package com.example.whatsinmyfridge.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/** Straffere, "clean" Radien statt der früheren Übersee-Rundungen - wirkt professioneller. */
internal val FridgeShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

/** Deutlich runder und bauchiger - für die verspielten Themes (Verspielt, Zitrus). */
internal val FridgePlayfulShapes = Shapes(
    extraSmall = RoundedCornerShape(14.dp),
    small = RoundedCornerShape(20.dp),
    medium = RoundedCornerShape(28.dp),
    large = RoundedCornerShape(36.dp),
    extraLarge = RoundedCornerShape(44.dp),
)

/** Fast rechtwinklig, plakativ - für den grafischen Kontrast-Modus. */
internal val FridgeSharpShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(10.dp),
    extraLarge = RoundedCornerShape(14.dp),
)

/** Vollständig abgerundete "Pille" für Buttons und Chips - der verspielte Signature-Look. */
val FridgePillShape = RoundedCornerShape(50)
