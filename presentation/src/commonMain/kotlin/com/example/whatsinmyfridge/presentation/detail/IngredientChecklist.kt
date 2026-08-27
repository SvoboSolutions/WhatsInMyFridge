package com.example.whatsinmyfridge.presentation.detail

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.whatsinmyfridge.core.theme.FridgeSpacing

/**
 * Klar getrennt: was schon da ist (grün, abgehakt) vs. was noch besorgt werden muss
 * (Akzentfarbe, Einkaufswagen). Reihenfolge bewusst "brauchst du noch" zuerst - das ist
 * die eigentlich handlungsrelevante Information beim Einkaufen.
 */
@Composable
fun IngredientChecklist(
    haveIngredients: List<String>,
    needIngredients: List<String>,
) {
    if (needIngredients.isNotEmpty()) {
        IngredientGroup(
            title = "Brauchst du noch (${needIngredients.size})",
            ingredients = needIngredients,
            icon = Icons.Filled.ShoppingCart,
            tint = MaterialTheme.colorScheme.tertiary,
        )
    }
    if (haveIngredients.isNotEmpty()) {
        IngredientGroup(
            title = "Hast du schon (${haveIngredients.size})",
            ingredients = haveIngredients,
            icon = Icons.Filled.CheckCircle,
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun IngredientGroup(title: String, ingredients: List<String>, icon: ImageVector, tint: Color) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = tint,
        modifier = Modifier.padding(horizontal = FridgeSpacing.md, vertical = FridgeSpacing.sm),
    )
    ingredients.forEach { ingredient ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = FridgeSpacing.md, vertical = FridgeSpacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.padding(end = FridgeSpacing.sm).size(18.dp))
            Text(ingredient, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
