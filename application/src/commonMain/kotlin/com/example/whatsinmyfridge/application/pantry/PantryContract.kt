package com.example.whatsinmyfridge.application.pantry

import com.example.whatsinmyfridge.domain.model.Ingredient

/** Verhindert eine unbrauchbar lange Liste (Anzeige/Performance) und versehentliches Zuspammen. */
const val MAX_PANTRY_SIZE = 150

data class PantryState(
    val ingredientInput: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val selectedForDeletion: Set<Ingredient> = emptySet(),
    val errorMessage: String? = null,
) {
    val isFull: Boolean get() = ingredients.size >= MAX_PANTRY_SIZE
}

sealed interface PantryIntent {
    data class UpdateIngredientInput(val value: String) : PantryIntent
    data object AddIngredient : PantryIntent
    data class AddIngredients(val names: List<String>) : PantryIntent

    /** Markiert/entmarkiert eine Zutat zum Löschen (Mehrfachauswahl statt Sofort-Löschen). */
    data class ToggleSelectForDeletion(val ingredient: Ingredient) : PantryIntent
    data object SelectAll : PantryIntent
    data object ClearSelection : PantryIntent
    data object DeleteSelected : PantryIntent

    data object DismissError : PantryIntent
}
