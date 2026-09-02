package com.example.whatsinmyfridge.presentation.saved

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.whatsinmyfridge.application.saved.SavedRecipesIntent
import com.example.whatsinmyfridge.application.saved.SavedRecipesState
import com.example.whatsinmyfridge.core.theme.FridgeSpacing
import com.example.whatsinmyfridge.domain.model.Recipe
import com.example.whatsinmyfridge.presentation.common.RecipeCard
import com.example.whatsinmyfridge.presentation.common.RecipeCardSkeleton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedRecipesScreen(
    state: SavedRecipesState,
    onIntent: (SavedRecipesIntent) -> Unit,
    onRecipeClick: (Recipe) -> Unit,
) {
    Scaffold(topBar = { TopAppBar(title = { Text("Gespeicherte Rezepte") }) }) { padding ->
        when {
            state.isLoading -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = FridgeSpacing.md),
                verticalArrangement = Arrangement.spacedBy(FridgeSpacing.sm + FridgeSpacing.xs),
                contentPadding = PaddingValues(top = FridgeSpacing.sm + FridgeSpacing.xs, bottom = FridgeSpacing.md),
            ) {
                items(3) { RecipeCardSkeleton() }
            }

            state.recipes.isEmpty() -> Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(top = FridgeSpacing.xxl),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    Icons.Filled.BookmarkBorder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(56.dp).padding(bottom = FridgeSpacing.sm + FridgeSpacing.xs),
                )
                Text(
                    "Noch keine Rezepte gespeichert",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    "Tippe bei einem Rezept auf das Lesezeichen",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                )
            }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = FridgeSpacing.md),
                verticalArrangement = Arrangement.spacedBy(FridgeSpacing.sm + FridgeSpacing.xs),
                contentPadding = PaddingValues(top = FridgeSpacing.sm + FridgeSpacing.xs, bottom = FridgeSpacing.md),
            ) {
                items(state.recipes, key = { it.id }) { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        isSaved = true,
                        onClick = { onRecipeClick(recipe) },
                        onToggleSave = { onIntent(SavedRecipesIntent.Remove(recipe.id)) },
                    )
                }
            }
        }
    }
}
