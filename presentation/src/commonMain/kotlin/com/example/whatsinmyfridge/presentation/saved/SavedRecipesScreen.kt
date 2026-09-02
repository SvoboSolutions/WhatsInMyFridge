package com.example.whatsinmyfridge.presentation.saved

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.whatsinmyfridge.application.saved.SavedRecipesIntent
import com.example.whatsinmyfridge.application.saved.SavedRecipesState
import com.example.whatsinmyfridge.core.theme.FridgeSpacing
import com.example.whatsinmyfridge.domain.model.Recipe
import com.example.whatsinmyfridge.presentation.common.EmptyStateColumn
import com.example.whatsinmyfridge.presentation.common.RecipeCard
import com.example.whatsinmyfridge.presentation.common.RecipeCardSkeleton

private enum class SavedRecipesView { LOADING, EMPTY, CONTENT }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedRecipesScreen(
    state: SavedRecipesState,
    onIntent: (SavedRecipesIntent) -> Unit,
    onRecipeClick: (Recipe) -> Unit,
) {
    val view = when {
        state.isLoading -> SavedRecipesView.LOADING
        state.recipes.isEmpty() -> SavedRecipesView.EMPTY
        else -> SavedRecipesView.CONTENT
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Gespeicherte Rezepte") }) }) { padding ->
        AnimatedContent(
            targetState = view,
            modifier = Modifier.fillMaxSize().padding(padding),
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "savedRecipesView",
        ) { targetView ->
            when (targetView) {
                SavedRecipesView.LOADING -> LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = FridgeSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(FridgeSpacing.smMd),
                    contentPadding = PaddingValues(top = FridgeSpacing.smMd, bottom = FridgeSpacing.md),
                ) {
                    items(3) { RecipeCardSkeleton() }
                }

                SavedRecipesView.EMPTY -> EmptyStateColumn(
                    icon = Icons.Filled.BookmarkBorder,
                    title = "Noch keine Rezepte gespeichert",
                    subtitle = "Tippe bei einem Rezept auf das Lesezeichen",
                )

                SavedRecipesView.CONTENT -> LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = FridgeSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(FridgeSpacing.smMd),
                    contentPadding = PaddingValues(top = FridgeSpacing.smMd, bottom = FridgeSpacing.md),
                ) {
                    items(state.recipes, key = { it.id }) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            isSaved = true,
                            onClick = { onRecipeClick(recipe) },
                            onToggleSave = { onIntent(SavedRecipesIntent.Remove(recipe.id)) },
                            modifier = Modifier.animateItem(),
                        )
                    }
                }
            }
        }
    }
}
