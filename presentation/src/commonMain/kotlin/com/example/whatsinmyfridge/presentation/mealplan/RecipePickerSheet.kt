package com.example.whatsinmyfridge.presentation.mealplan

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.whatsinmyfridge.core.theme.FridgeSpacing
import com.example.whatsinmyfridge.domain.model.Recipe
import com.example.whatsinmyfridge.presentation.common.RecipeCard

/**
 * Manuelle Zuweisung: Auswahl eines gespeicherten Rezepts für einen bestimmten Tag,
 * unabhängig von den automatischen Vorschlägen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipePickerSheet(
    savedRecipes: List<Recipe>,
    onPick: (Recipe) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = FridgeSpacing.md)) {
            Text(
                "Rezept auswählen",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = FridgeSpacing.sm),
            )

            if (savedRecipes.isEmpty()) {
                Text(
                    "Noch keine gespeicherten Rezepte. Speichere zuerst Rezepte aus der Suche.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = FridgeSpacing.lg),
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = FridgeSpacing.lg),
                ) {
                    items(savedRecipes, key = { it.id }) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            isSaved = true,
                            onClick = { onPick(recipe) },
                            onToggleSave = { onPick(recipe) },
                            modifier = Modifier.padding(bottom = FridgeSpacing.sm),
                        )
                    }
                }
            }
        }
    }
}
