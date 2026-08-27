package com.example.whatsinmyfridge.presentation.saved

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.whatsinmyfridge.application.saved.SavedRecipesViewModel
import com.example.whatsinmyfridge.domain.model.Recipe
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SavedRecipesRoute(
    onRecipeClick: (Recipe) -> Unit,
    viewModel: SavedRecipesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    SavedRecipesScreen(state = state, onIntent = viewModel::onIntent, onRecipeClick = onRecipeClick)
}
