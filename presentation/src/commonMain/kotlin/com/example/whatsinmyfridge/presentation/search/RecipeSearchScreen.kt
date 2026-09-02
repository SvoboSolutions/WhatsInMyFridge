package com.example.whatsinmyfridge.presentation.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SoupKitchen
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.whatsinmyfridge.application.search.RecipeSearchIntent
import com.example.whatsinmyfridge.application.search.RecipeSearchState
import com.example.whatsinmyfridge.core.theme.FridgePillShape
import com.example.whatsinmyfridge.core.theme.FridgeSpacing
import com.example.whatsinmyfridge.domain.model.Recipe
import com.example.whatsinmyfridge.presentation.common.RecipeCard
import com.example.whatsinmyfridge.presentation.common.RecipeCardSkeleton

/**
 * Reine UI: bekommt fertigen State + sendet Intents. Keine ViewModel-/DI-Kenntnis.
 *
 * Such-Header und Ergebnisliste teilen sich eine LazyColumn: der Header ist das erste Item
 * und scrollt mit weg, sobald man durch die Rezepte scrollt - so bleibt mehr Platz für die Liste.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeSearchScreen(
    state: RecipeSearchState,
    onIntent: (RecipeSearchIntent) -> Unit,
    onRecipeClick: (Recipe) -> Unit,
    onOpenPhotoRecognition: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            onIntent(RecipeSearchIntent.DismissError)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Was ist im Kühlschrank?") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(
                start = FridgeSpacing.md,
                end = FridgeSpacing.md,
                bottom = FridgeSpacing.md,
            ),
        ) {
            item {
                SearchHeader(state = state, onIntent = onIntent, onOpenPhotoRecognition = onOpenPhotoRecognition)
            }

            searchResults(state = state, onIntent = onIntent, onRecipeClick = onRecipeClick)
        }
    }
}

@Composable
private fun SearchHeader(
    state: RecipeSearchState,
    onIntent: (RecipeSearchIntent) -> Unit,
    onOpenPhotoRecognition: () -> Unit,
) {
    Column {
        Text(
            "Zutaten eingeben, die du zuhause hast",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = FridgeSpacing.sm + FridgeSpacing.xs, bottom = FridgeSpacing.sm),
        )

        OutlinedTextField(
            value = state.ingredientInput,
            onValueChange = { onIntent(RecipeSearchIntent.UpdateIngredientInput(it)) },
            label = { Text("z.B. Tomate") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            trailingIcon = {
                Row {
                    IconButton(onClick = onOpenPhotoRecognition) {
                        Icon(Icons.Filled.CameraAlt, contentDescription = "Per Foto erkennen")
                    }
                    IconButton(onClick = { onIntent(RecipeSearchIntent.AddIngredient) }) {
                        Icon(Icons.Filled.Add, contentDescription = "Hinzufügen")
                    }
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth(),
        )

        if (state.ingredients.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(FridgeSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(FridgeSpacing.sm),
                modifier = Modifier.padding(top = FridgeSpacing.sm + FridgeSpacing.xs).fillMaxWidth(),
            ) {
                state.ingredients.forEach { ingredient ->
                    FilterChip(
                        selected = true,
                        onClick = { onIntent(RecipeSearchIntent.RemoveIngredient(ingredient)) },
                        label = { Text(ingredient.name) },
                        shape = FridgePillShape,
                        trailingIcon = {
                            Icon(Icons.Filled.Close, contentDescription = "Entfernen", modifier = Modifier.padding(2.dp))
                        },
                    )
                }
            }
        }

        Button(
            onClick = { onIntent(RecipeSearchIntent.Search) },
            enabled = state.canSearch,
            shape = FridgePillShape,
            modifier = Modifier.padding(vertical = FridgeSpacing.md).fillMaxWidth().height(52.dp),
        ) {
            Text("Rezepte suchen")
        }
    }
}

private fun LazyListScope.searchResults(
    state: RecipeSearchState,
    onIntent: (RecipeSearchIntent) -> Unit,
    onRecipeClick: (Recipe) -> Unit,
) {
    when {
        state.isLoading -> items(3) {
            RecipeCardSkeleton(modifier = Modifier.padding(bottom = FridgeSpacing.sm + FridgeSpacing.xs))
        }

        state.recipes.isEmpty() -> item { EmptySearchState(hasSearched = state.hasSearched) }

        else -> items(state.recipes, key = { it.id }) { recipe ->
            RecipeCard(
                recipe = recipe,
                isSaved = recipe.id in state.savedRecipeIds,
                onClick = { onRecipeClick(recipe) },
                onToggleSave = { onIntent(RecipeSearchIntent.ToggleSaveRecipe(recipe)) },
            )
            Box(Modifier.height(FridgeSpacing.sm + FridgeSpacing.xs))
        }
    }
}

@Composable
private fun EmptySearchState(hasSearched: Boolean) {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = FridgeSpacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            Icons.Filled.SoupKitchen,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(56.dp).padding(bottom = FridgeSpacing.sm + FridgeSpacing.xs),
        )
        if (hasSearched) {
            Text(
                "Keine Treffer für diese Zutaten",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                "Versuch es mit mehr Zutaten oder prüfe deine Diät-/Allergie-Filter im Profil",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
            )
        } else {
            Text(
                "Noch keine Rezepte",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                "Füge Zutaten hinzu und starte die Suche",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
            )
        }
    }
}
