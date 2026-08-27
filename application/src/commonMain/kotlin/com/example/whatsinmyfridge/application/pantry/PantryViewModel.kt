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
                _state.update { it.copy(ingredients = ingredients) }
            }
        }
    }

    fun onIntent(intent: PantryIntent) {
        when (intent) {
            is PantryIntent.UpdateIngredientInput ->
                _state.update { it.copy(ingredientInput = intent.value) }

            PantryIntent.AddIngredient -> addFromInput()

            is PantryIntent.AddIngredients -> addAll(intent.names)

            is PantryIntent.RemoveIngredient -> remove(intent.ingredient)

            PantryIntent.DismissError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun addFromInput() {
        val name = _state.value.ingredientInput.trim()
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
            names.map { it.trim() }.filter { it.isNotEmpty() }.forEach { name ->
                runCatching { addPantryIngredient(Ingredient(name)) }
                    .onFailure { error -> Logger.e(TAG, "Zutat konnte nicht hinzugefügt werden", error) }
            }
        }
    }

    private fun remove(ingredient: Ingredient) {
        viewModelScope.launch { removePantryIngredient(ingredient) }
    }
}
