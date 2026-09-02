package com.example.whatsinmyfridge.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.whatsinmyfridge.core.theme.FridgeSpacing
import com.example.whatsinmyfridge.domain.model.NutritionInfo
import kotlin.math.roundToInt

/** Nährwerte pro Portion als vier gleich große Stat-Kacheln - "auf einen Blick", nicht als Liste. */
@Composable
fun NutritionRow(nutrition: NutritionInfo, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = FridgeSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(FridgeSpacing.sm),
    ) {
        nutrition.calories?.let { NutritionStat("${it}", "kcal", Modifier.weight(1f)) }
        nutrition.proteinG?.let { NutritionStat("${it.roundToInt()}", "g Protein", Modifier.weight(1f)) }
        nutrition.fatG?.let { NutritionStat("${it.roundToInt()}", "g Fett", Modifier.weight(1f)) }
        nutrition.carbsG?.let { NutritionStat("${it.roundToInt()}", "g Kohlenh.", Modifier.weight(1f)) }
    }
}

@Composable
private fun NutritionStat(value: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(vertical = FridgeSpacing.sm),
        ) {
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
