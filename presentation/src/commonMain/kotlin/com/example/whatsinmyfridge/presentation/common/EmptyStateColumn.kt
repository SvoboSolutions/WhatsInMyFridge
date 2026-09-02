package com.example.whatsinmyfridge.presentation.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.whatsinmyfridge.core.theme.FridgeSpacing

/**
 * Gemeinsamer "leerer Zustand" für Suche/Vorräte/Gespeichert/Wochenplan: kleine Illustration
 * (IllustrationBadge, gleiches Motiv wie im Onboarding) statt nacktem Icon, Titel + Untertitel.
 * Eine Stelle statt drei fast identischer Kopien pro Screen.
 */
@Composable
fun EmptyStateColumn(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(top = FridgeSpacing.xxl, start = FridgeSpacing.lg, end = FridgeSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IllustrationBadge(icon, modifier = Modifier.padding(bottom = FridgeSpacing.md))
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = FridgeSpacing.xs),
        )
        Text(
            subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
        )
    }
}
