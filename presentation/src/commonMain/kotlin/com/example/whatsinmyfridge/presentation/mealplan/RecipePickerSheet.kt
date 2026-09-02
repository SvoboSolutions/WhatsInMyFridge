package com.example.whatsinmyfridge.presentation.mealplan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.whatsinmyfridge.core.theme.FridgePillShape
import com.example.whatsinmyfridge.core.theme.FridgeSpacing
import com.example.whatsinmyfridge.domain.model.Recipe
import com.example.whatsinmyfridge.presentation.common.RecipeCard

/**
 * Rezept für einen Tag zuweisen: entweder einen frischen Vorschlag anfragen (Quelle je
 * nach Profil-Einstellung Datenbank oder KI - für diesen Screen unsichtbar, siehe
 * SuggestRecipesForDayUseCase) oder aus den bereits gespeicherten Rezepten wählen.
 * Ein Klick auf "Vorschlag holen" löst genau eine Anfrage aus, nicht mehr.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipePickerSheet(
    savedRecipes: List<Recipe>,
    suggestions: List<Recipe>,
    isSuggesting: Boolean,
    suggestionError: String?,
    allowExtraIngredients: Boolean,
    onAllowExtraIngredientsChange: (Boolean) -> Unit,
    onRequestSuggestions: () -> Unit,
    onPick: (Recipe) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(horizontal = FridgeSpacing.md),
            contentPadding = PaddingValues(bottom = FridgeSpacing.lg),
        ) {
            item {
                Text(
                    "Rezeptvorschlag",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = FridgeSpacing.sm),
                )
            }
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(FridgeSpacing.sm),
                    modifier = Modifier.padding(bottom = FridgeSpacing.sm),
                ) {
                    FilterChip(
                        selected = !allowExtraIngredients,
                        onClick = { onAllowExtraIngredientsChange(false) },
                        label = { Text("Nur Vorräte") },
                    )
                    FilterChip(
                        selected = allowExtraIngredients,
                        onClick = { onAllowExtraIngredientsChange(true) },
                        label = { Text("Mit Einkauf") },
                    )
                }
            }
            item {
                Button(
                    onClick = onRequestSuggestions,
                    enabled = !isSuggesting,
                    shape = FridgePillShape,
                    modifier = Modifier.fillMaxWidth().padding(bottom = FridgeSpacing.md),
                ) {
                    if (isSuggesting) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.padding(end = FridgeSpacing.sm))
                        Text(if (suggestions.isEmpty()) "Vorschlag holen" else "Neuer Vorschlag")
                    }
                }
            }
            if (suggestionError != null) {
                item {
                    Text(
                        suggestionError,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = FridgeSpacing.md),
                    )
                }
            }
            items(suggestions, key = { "suggestion-${it.id}" }) { recipe ->
                RecipeCard(
                    recipe = recipe,
                    isSaved = false,
                    onClick = { onPick(recipe) },
                    onToggleSave = { onPick(recipe) },
                    modifier = Modifier.padding(bottom = FridgeSpacing.sm),
                )
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = FridgeSpacing.md))
                Text(
                    "Gespeicherte Rezepte",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = FridgeSpacing.sm),
                )
            }

            if (savedRecipes.isEmpty()) {
                item {
                    Text(
                        "Noch keine gespeicherten Rezepte. Speichere zuerst Rezepte aus der Suche.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                items(savedRecipes, key = { "saved-${it.id}" }) { recipe ->
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
