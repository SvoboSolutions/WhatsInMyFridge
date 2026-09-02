package com.example.whatsinmyfridge.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.whatsinmyfridge.core.theme.FridgePillShape
import com.example.whatsinmyfridge.core.theme.FridgeSpacing
import com.example.whatsinmyfridge.core.theme.ThemeSwatch
import com.example.whatsinmyfridge.core.theme.themeSwatch
import com.example.whatsinmyfridge.domain.model.ThemeMode

/**
 * Zeigt jedes Theme als kleine Farbvorschau-Kachel statt als reinen Text/Icon-Button -
 * man sieht direkt, wie das Theme aussehen wird, bevor man es antippt.
 */
@Composable
fun ThemeModeSelector(
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSystemDark = isSystemInDarkTheme()
    val rows = ThemeMode.entries.chunked(2)

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(FridgeSpacing.sm)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(FridgeSpacing.sm), modifier = Modifier.fillMaxWidth()) {
                row.forEach { mode ->
                    ThemeSwatchTile(
                        mode = mode,
                        swatch = themeSwatch(mode, isSystemDark),
                        isSelected = mode == selected,
                        onClick = { onSelect(mode) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeSwatchTile(
    mode: ThemeMode,
    swatch: ThemeSwatch,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .aspectRatio(1.35f)
            .selectable(selected = isSelected, onClick = onClick, role = Role.RadioButton),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = swatch.background),
        border = BorderStroke(
            width = if (isSelected) 2.5.dp else 1.dp,
            color = if (isSelected) swatch.primary else MaterialTheme.colorScheme.outlineVariant,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 0.dp),
    ) {
        Box(Modifier.fillMaxWidth().padding(FridgeSpacing.sm)) {
            if (isSelected) {
                Surface(
                    shape = CircleShape,
                    color = swatch.primary,
                    modifier = Modifier.align(Alignment.TopEnd).size(20.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            tint = swatch.background,
                            modifier = Modifier.size(13.dp),
                        )
                    }
                }
            }

            Column(Modifier.align(Alignment.BottomStart)) {
                Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                    ColorDot(swatch.primary)
                    ColorDot(swatch.secondary)
                    ColorDot(swatch.tertiary)
                }
                Text(
                    mode.label(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColorFor(swatch.background),
                    modifier = Modifier.padding(top = FridgeSpacing.xs),
                )
            }
        }
    }
}

@Composable
private fun ColorDot(color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = FridgePillShape,
        color = color,
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.surface),
        modifier = modifier.size(22.dp).clip(FridgePillShape),
    ) {}
}

/** Grobe Luminanz-Schätzung, damit das Label auf jedem Untergrund lesbar bleibt. */
private fun contentColorFor(background: Color): Color {
    val luminance = 0.299f * background.red + 0.587f * background.green + 0.114f * background.blue
    return if (luminance > 0.6f) Color(0xFF1A1108) else Color(0xFFFFF6EC)
}
