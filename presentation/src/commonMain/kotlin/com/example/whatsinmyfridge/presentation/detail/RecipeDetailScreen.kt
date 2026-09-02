package com.example.whatsinmyfridge.presentation.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.whatsinmyfridge.application.detail.RecipeDetailIntent
import com.example.whatsinmyfridge.application.detail.RecipeDetailState
import com.example.whatsinmyfridge.core.theme.FridgePillShape
import com.example.whatsinmyfridge.core.theme.FridgeSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    state: RecipeDetailState,
    onIntent: (RecipeDetailIntent) -> Unit,
    onBack: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var isCookModeOpen by remember { mutableStateOf(false) }

    LaunchedEffect(state.justCookedPoints) {
        state.justCookedPoints?.let { points ->
            snackbarHostState.showSnackbar("+$points Punkte - nichts verschwendet!")
            onIntent(RecipeDetailIntent.DismissCookedFeedback)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.details?.title ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                },
                actions = {
                    if (state.details != null) {
                        IconButton(onClick = { onIntent(RecipeDetailIntent.ToggleSave) }) {
                            Icon(
                                if (state.isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                contentDescription = if (state.isSaved) "Gespeichert" else "Speichern",
                            )
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            val details = state.details
            if (details != null) {
                Column {
                    if (details.instructions.isNotEmpty()) {
                        Button(
                            onClick = { isCookModeOpen = true },
                            shape = FridgePillShape,
                            modifier = Modifier.padding(horizontal = FridgeSpacing.md, vertical = FridgeSpacing.sm)
                                .fillMaxWidth().height(52.dp),
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null)
                            Text("Jetzt kochen", modifier = Modifier.padding(start = FridgeSpacing.sm))
                        }
                    }
                    CookedButton(onClick = { onIntent(RecipeDetailIntent.MarkAsCooked) })
                }
            }
        },
    ) { padding ->
        val details = state.details
        val errorMessage = state.errorMessage
        val view = when {
            state.isLoading -> RecipeDetailView.LOADING
            errorMessage != null -> RecipeDetailView.ERROR
            else -> RecipeDetailView.CONTENT
        }

        AnimatedContent(
            targetState = view,
            modifier = Modifier.fillMaxSize().padding(padding),
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "recipeDetailView",
        ) { targetView ->
            when (targetView) {
                RecipeDetailView.LOADING -> RecipeDetailSkeleton()

                RecipeDetailView.ERROR -> Box(
                    Modifier.fillMaxSize().padding(FridgeSpacing.lg),
                    contentAlignment = Alignment.Center,
                ) { Text(errorMessage.orEmpty(), color = MaterialTheme.colorScheme.error) }

                RecipeDetailView.CONTENT -> if (details != null) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item { RecipeDetailHeader(details) }

                        item { IngredientChecklist(state.haveIngredients, state.needIngredients) }

                        details.nutrition?.let { nutrition ->
                            item { NutritionRow(nutrition, modifier = Modifier.padding(top = FridgeSpacing.sm)) }
                        }

                        if (details.ingredients.isNotEmpty()) {
                            item {
                                ServingsIngredientSection(
                                    ingredients = details.ingredients,
                                    baseServings = details.servings ?: 1,
                                    displayServings = state.displayServings,
                                    onServingsChange = { onIntent(RecipeDetailIntent.SetServings(it)) },
                                    modifier = Modifier.padding(top = FridgeSpacing.md),
                                )
                            }
                        }

                        if (details.summary.isNotBlank()) {
                            item {
                                Text(
                                    details.summary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(horizontal = FridgeSpacing.md, vertical = FridgeSpacing.md),
                                )
                            }
                        }

                        if (details.instructions.isNotEmpty()) {
                            item { SectionTitle("Zubereitung") }
                            itemsIndexed(details.instructions) { index, step ->
                                Text(
                                    "${index + 1}. $step",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(horizontal = FridgeSpacing.md, vertical = 6.dp),
                                )
                            }
                        }
                    }
                } else {
                    Box(Modifier.fillMaxSize())
                }
            }
        }
    }

    val cookModeDetails = state.details
    if (isCookModeOpen && cookModeDetails != null) {
        CookModeOverlay(
            recipeTitle = cookModeDetails.title,
            steps = cookModeDetails.instructions,
            onFinish = {
                isCookModeOpen = false
                onIntent(RecipeDetailIntent.MarkAsCooked)
            },
            onClose = { isCookModeOpen = false },
        )
    }
}

private enum class RecipeDetailView { LOADING, ERROR, CONTENT }

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = FridgeSpacing.md, vertical = FridgeSpacing.sm),
    )
}
