package com.example.whatsinmyfridge.application.pantry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsinmyfridge.core.logging.Logger
import com.example.whatsinmyfridge.domain.model.Ingredient
import com.example.whatsinmyfridge.domain.usecase.AddPantryIngredientUseCase
import com.example.whatsinmyfridge.domain.usecase.ObservePantryUseCase
import com.example.whatsinmyfridge.domain.usecase.RemovePantryIngredientUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "PantryViewModel"

class PantryViewModel(
    private val observePantry: ObservePantryUseCase,
    private val addPantryIngredient: AddPantryIngredientUseCase,
    private val removePantryIngredient: RemovePantryIngredientUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(PantryState())
    val state: StateFlow<PantryState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observePantry().collect { ingredients ->
                _state.update {
                    it.copy(
                        ingredients = ingredients,
                        // Zutaten, die inzwischen aus der DB verschwunden sind, aus der Auswahl entfernen.
                        selectedForDeletion = it.selectedForDeletion.intersect(ingredients.toSet()),
                    )
                }
            }
        }
    }

    fun onIntent(intent: PantryIntent) {
        when (intent) {
            is PantryIntent.UpdateIngredientInput ->
                _state.update { it.copy(ingredientInput = intent.value) }

            PantryIntent.AddIngredient -> addFromInput()

            is PantryIntent.AddIngredients -> addAll(intent.names)

            is PantryIntent.ToggleSelectForDeletion -> toggleSelection(intent.ingredient)

            PantryIntent.ClearSelection -> _state.update { it.copy(selectedForDeletion = emptySet()) }

            PantryIntent.DeleteSelected -> deleteSelected()

            PantryIntent.DismissError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun toggleSelection(ingredient: Ingredient) {
        _state.update {
            val selected = it.selectedForDeletion
            it.copy(
                selectedForDeletion = if (ingredient in selected) selected - ingredient else selected + ingredient,
            )
        }
    }

    private fun deleteSelected() {
        val toDelete = _state.value.selectedForDeletion
        if (toDelete.isEmpty()) return
        _state.update { it.copy(selectedForDeletion = emptySet()) }
        viewModelScope.launch {
            toDelete.forEach { ingredient -> removePantryIngredient(ingredient) }
        }
    }

    private fun addFromInput() {
        val name = _state.value.ingredientInput.trim().lowercase()
        if (name.isEmpty()) return
        _state.update { it.copy(ingredientInput = "") }
        viewModelScope.launch {
            runCatching { addPantryIngredient(Ingredient(name)) }
                .onFailure { error ->
                    Logger.e(TAG, "Zutat konnte nicht hinzugefügt werden", error)
                    _state.update { it.copy(errorMessage = error.message ?: "Unbekannter Fehler") }
                }
        }
    }

    private fun addAll(names: List<String>) {
        if (names.isEmpty()) return
        viewModelScope.launch {
            names.map { it.trim().lowercase() }.filter { it.isNotEmpty() }.distinct().forEach { name ->
                runCatching { addPantryIngredient(Ingredient(name)) }
                    .onFailure { error -> Logger.e(TAG, "Zutat konnte nicht hinzugefügt werden", error) }
            }
        }
    }
}
