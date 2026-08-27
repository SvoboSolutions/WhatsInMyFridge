package com.example.whatsinmyfridge.application.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsinmyfridge.core.logging.Logger
import com.example.whatsinmyfridge.domain.model.Ingredient
import com.example.whatsinmyfridge.domain.model.Recipe
import com.example.whatsinmyfridge.domain.usecase.ObserveSavedRecipeIdsUseCase
import com.example.whatsinmyfridge.domain.usecase.RemoveSavedRecipeUseCase
import com.example.whatsinmyfridge.domain.usecase.SaveRecipeUseCase
import com.example.whatsinmyfridge.domain.usecase.SearchRecipesByIngredientsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "RecipeSearchViewModel"

class RecipeSearchViewModel(
    private val searchRecipesByIngredients: SearchRecipesByIngredientsUseCase,
    private val observeSavedRecipeIds: ObserveSavedRecipeIdsUseCase,
    private val saveRecipe: SaveRecipeUseCase,
    private val removeSavedRecipe: RemoveSavedRecipeUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(RecipeSearchState())
    val state: StateFlow<RecipeSearchState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeSavedRecipeIds().collect { ids ->
                _state.update { it.copy(savedRecipeIds = ids) }
            }
        }
    }

    fun onIntent(intent: RecipeSearchIntent) {
        when (intent) {
            is RecipeSearchIntent.UpdateIngredientInput ->
                _state.update { it.copy(ingredientInput = intent.value) }

            RecipeSearchIntent.AddIngredient -> addIngredient()

            is RecipeSearchIntent.AddIngredients -> addIngredients(intent.names)

            is RecipeSearchIntent.RemoveIngredient ->
                _state.update { it.copy(ingredients = it.ingredients - intent.ingredient) }

            RecipeSearchIntent.Search -> search()

            is RecipeSearchIntent.ToggleSaveRecipe -> toggleSave(intent.recipe)

            RecipeSearchIntent.DismissError ->
                _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun addIngredient() {
        val name = _state.value.ingredientInput.trim()
        if (name.isEmpty()) return
        _state.update {
            it.copy(
                ingredients = it.ingredients + Ingredient(name),
                ingredientInput = "",
            )
        }
    }

    private fun addIngredients(names: List<String>) {
        if (names.isEmpty()) return
        _state.update { current ->
            val existingNames = current.ingredients.map { it.name.lowercase() }.toMutableSet()
            val newIngredients = names
                .map { it.trim() }
                .filter { it.isNotEmpty() && existingNames.add(it.lowercase()) }
                .map { Ingredient(it) }
            current.copy(ingredients = current.ingredients + newIngredients)
        }
    }

    private fun search() {
        val ingredients = _state.value.ingredients
        if (ingredients.isEmpty()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            searchRecipesByIngredients(ingredients)
                .onSuccess { recipes ->
                    _state.update { it.copy(isLoading = false, recipes = recipes) }
                }
                .onFailure { error ->
                    Logger.e(TAG, "Rezeptsuche fehlgeschlagen", error)
                    _state.update {
                        it.copy(isLoading = false, errorMessage = error.message ?: "Unbekannter Fehler")
                    }
                }
        }
    }

    private fun toggleSave(recipe: Recipe) {
        viewModelScope.launch {
            if (recipe.id in _state.value.savedRecipeIds) {
                removeSavedRecipe(recipe.id)
            } else {
                saveRecipe(recipe)
            }
        }
    }
}
