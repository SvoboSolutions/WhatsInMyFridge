package com.example.whatsinmyfridge.presentation.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.whatsinmyfridge.core.theme.FridgePillShape
import com.example.whatsinmyfridge.core.theme.FridgeSpacing
import com.example.whatsinmyfridge.domain.model.RecipeIngredient
import kotlin.math.roundToInt

/**
 * Zutatenliste mit +/- Stepper für die Portionsanzahl - Mengen werden proportional zur
 * ursprünglichen Portionsangabe umgerechnet. Zutaten ohne erkannte Mengenangabe (amount=0,
 * z.B. "Salz nach Geschmack") bleiben unverändert, damit nichts Unsinniges wie "0.0x Salz"
 * angezeigt wird.
 */
@Composable
fun ServingsIngredientSection(
    ingredients: List<RecipeIngredient>,
    baseServings: Int,
    displayServings: Int,
    onServingsChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(horizontal = FridgeSpacing.md)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = FridgeSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Zutaten",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            ServingsStepper(servings = displayServings, onChange = onServingsChange)
        }

        val scale = if (baseServings > 0) displayServings.toFloat() / baseServings else 1f

        ingredients.forEach { ingredient ->
            Text(
                ingredient.scaledLabel(scale),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth().padding(vertical = FridgeSpacing.xs),
            )
        }
    }
}

@Composable
private fun ServingsStepper(servings: Int, onChange: (Int) -> Unit) {
    Surface(shape = FridgePillShape, color = MaterialTheme.colorScheme.secondaryContainer) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onChange(servings - 1) }, enabled = servings > 1, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Filled.Remove, contentDescription = "Weniger Portionen", modifier = Modifier.size(18.dp))
            }
            Text(
                "$servings",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = FridgeSpacing.xs),
            )
            IconButton(onClick = { onChange(servings + 1) }, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Filled.Add, contentDescription = "Mehr Portionen", modifier = Modifier.size(18.dp))
            }
        }
    }
}

private fun RecipeIngredient.scaledLabel(scale: Float): String {
    if (amount <= 0.0) return original
    val scaledAmount = (amount * scale).toFloat()
    val displayName = name.ifBlank { original }
    return listOf(formatQuarter(scaledAmount), unit, displayName).filter { it.isNotBlank() }.joinToString(" ")
}

/** Rundet auf Viertel (1, 1¼, 1½, 1¾, 2, ...) - lesbarer als lange Dezimalzahlen beim Kochen. */
private fun formatQuarter(value: Float): String {
    val roundedQuarters = (value * 4).roundToInt()
    val whole = roundedQuarters / 4
    val fractionText = when (roundedQuarters % 4) {
        1 -> "¼"
        2 -> "½"
        3 -> "¾"
        else -> ""
    }
    return when {
        whole == 0 && fractionText.isNotEmpty() -> fractionText
        fractionText.isEmpty() -> whole.toString()
        else -> "$whole $fractionText"
    }
}
