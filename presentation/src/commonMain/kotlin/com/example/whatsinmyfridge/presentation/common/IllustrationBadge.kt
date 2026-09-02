package com.example.whatsinmyfridge.presentation.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Kleine "Illustration" aus geschichteten Kreisen statt eines nackten Icons - ein weicher
 * großer Hintergrund-Blob, zwei versetzte Akzent-Punkte in Sekundär-/Tertiärfarbe und ein
 * kräftig gefüllter Kreis mit dem eigentlichen Icon obendrauf. Kein SVG-Asset nötig, wirkt
 * aber "gestalteter" als ein einzelnes getöntes Icon.
 */
@Composable
fun IllustrationBadge(icon: ImageVector, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(112.dp), contentAlignment = Alignment.Center) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(112.dp),
        ) {}

        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.tertiaryContainer,
            modifier = Modifier.size(26.dp).align(Alignment.TopEnd),
        ) {}

        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.size(18.dp).align(Alignment.BottomStart),
        ) {}

        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(72.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(34.dp),
                )
            }
        }
    }
}
