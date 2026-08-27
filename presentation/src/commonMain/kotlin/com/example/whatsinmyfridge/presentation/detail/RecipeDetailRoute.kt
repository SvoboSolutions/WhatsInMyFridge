package com.example.whatsinmyfridge.presentation.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.whatsinmyfridge.application.detail.RecipeDetailViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun RecipeDetailRoute(
    recipeId: Long,
    usedIngredientNames: List<String>,
    missedIngredientNames: List<String>,
    onBack: () -> Unit,
    viewModel: RecipeDetailViewModel = koinViewModel(
        parameters = { parametersOf(recipeId, usedIngredientNames, missedIngredientNames) },
    ),
) {
    val state by viewModel.state.collectAsState()
    RecipeDetailScreen(state = state, onIntent = viewModel::onIntent, onBack = onBack)
}
