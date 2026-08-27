package com.example.whatsinmyfridge.application.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsinmyfridge.domain.model.Recipe
import com.example.whatsinmyfridge.domain.usecase.ObserveSavedRecipesUseCase
import com.example.whatsinmyfridge.domain.usecase.RemoveSavedRecipeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SavedRecipesState(
    val isLoading: Boolean = true,
    val recipes: List<Recipe> = emptyList(),
)

sealed interface SavedRecipesIntent {
    data class Remove(val recipeId: Long) : SavedRecipesIntent
}

class SavedRecipesViewModel(
    observeSavedRecipes: ObserveSavedRecipesUseCase,
    private val removeSavedRecipe: RemoveSavedRecipeUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SavedRecipesState())
    val state: StateFlow<SavedRecipesState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeSavedRecipes().collect { recipes ->
                _state.update { it.copy(isLoading = false, recipes = recipes) }
            }
        }
    }

    fun onIntent(intent: SavedRecipesIntent) {
        when (intent) {
            is SavedRecipesIntent.Remove -> viewModelScope.launch { removeSavedRecipe(intent.recipeId) }
        }
    }
}
