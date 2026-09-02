package com.example.whatsinmyfridge.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.whatsinmyfridge.core.theme.FridgePillShape
import com.example.whatsinmyfridge.core.theme.FridgeSpacing
import com.example.whatsinmyfridge.core.theme.ThemeSwatch
import com.example.whatsinmyfridge.core.theme.themeSwatch
import com.example.whatsinmyfridge.domain.model.ThemeMode

/**
 * Zeigt jedes Theme als Mini-Mockup (Titelbalken, Karte, Akzent-Punkt in den echten
 * Farben) statt als reinen Text/Icon-Button - fühlt sich an wie ein kleiner
 * Screenshot des Themes statt wie drei abstrakte Farbpunkte.
 */
@Composable
fun ThemeModeSelector(
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSystemDark = isSystemInDarkTheme()
    val rows = ThemeMode.entries.chunked(2)

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(FridgeSpacing.md)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(FridgeSpacing.md), modifier = Modifier.fillMaxWidth()) {
                row.forEach { mode ->
                    ThemeSwatchTile(
                        mode = mode,
                        swatch = themeSwatch(mode, isSystemDark),
                        isSelected = mode == selected,
                        onClick = { onSelect(mode) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size == 1) {
                    Box(Modifier.weight(1f))
                }
            }
        }
    }
}

private val PreviewShape = RoundedCornerShape(14.dp)

@Composable
private fun ThemeSwatchTile(
    mode: ThemeMode,
    swatch: ThemeSwatch,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.selectable(selected = isSelected, onClick = onClick, role = Role.RadioButton),
        verticalArrangement = Arrangement.spacedBy(FridgeSpacing.sm),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.45f)
                .clip(PreviewShape)
                .background(swatch.background)
                .border(
                    width = if (isSelected) 2.5.dp else 1.dp,
                    color = if (isSelected) swatch.primary else swatch.tertiary.copy(alpha = 0.25f),
                    shape = PreviewShape,
                ),
        ) {
            // Titelbalken-Akzent
            Box(
                Modifier
                    .align(Alignment.TopStart)
                    .padding(FridgeSpacing.sm)
                    .size(width = 30.dp, height = 8.dp)
                    .clip(FridgePillShape)
                    .background(swatch.primary),
            )
            // Karten-Andeutung
            Box(
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(FridgeSpacing.sm)
                    .fillMaxWidth(0.55f)
                    .height(22.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(swatch.secondary),
            )
            // Akzent-Punkt (z.B. FAB)
            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(FridgeSpacing.sm)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(swatch.tertiary),
            )
            if (isSelected) {
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(swatch.primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = swatch.background,
                        modifier = Modifier.size(13.dp),
                    )
                }
            }
        }

        Text(
            mode.label(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
