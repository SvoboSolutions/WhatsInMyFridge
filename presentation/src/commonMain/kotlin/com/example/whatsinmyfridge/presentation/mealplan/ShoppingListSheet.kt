package com.example.whatsinmyfridge.presentation.mealplan

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.example.whatsinmyfridge.core.theme.FridgeSpacing
import com.example.whatsinmyfridge.domain.model.ShoppingListEntry

/**
 * Fehlende Zutaten aller aktuell geplanten Rezepte der Woche, gruppiert nach Rezept. Kein
 * eigener State im ViewModel für "abgehakt" nötig fürs erste - reicht als lokaler Ankreuz-
 * Zettel für den Einkauf, wird beim Schließen zurückgesetzt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListSheet(
    isLoading: Boolean,
    entries: List<ShoppingListEntry>,
    checkedItems: Set<String>,
    onToggleItem: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = FridgeSpacing.md)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = FridgeSpacing.sm)) {
                Icon(Icons.Filled.ShoppingCart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    "Einkaufsliste",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = FridgeSpacing.sm),
                )
            }

            when {
                isLoading -> Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = FridgeSpacing.xl),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator() }

                entries.isEmpty() -> Text(
                    "Für deinen aktuellen Plan fehlt nichts - oder es sind noch keine Rezepte geplant.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = FridgeSpacing.xl),
                )

                else -> LazyColumn(
                    contentPadding = PaddingValues(bottom = FridgeSpacing.xl),
                ) {
                    entries.forEach { entry ->
                        item(key = "header-${entry.date}-${entry.recipeTitle}") {
                            Text(
                                entry.recipeTitle,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = FridgeSpacing.md, bottom = FridgeSpacing.xs),
                            )
                        }
                        items(entry.missingIngredients, key = { "${entry.date}-${entry.recipeTitle}-$it" }) { ingredient ->
                            val isChecked = ingredient in checkedItems
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().padding(vertical = FridgeSpacing.xs),
                            ) {
                                Checkbox(checked = isChecked, onCheckedChange = { onToggleItem(ingredient) })
                                Text(
                                    ingredient,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textDecoration = if (isChecked) TextDecoration.LineThrough else null,
                                    color = if (isChecked) {
                                        MaterialTheme.colorScheme.outline
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
