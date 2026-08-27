package com.example.whatsinmyfridge.application.pantry

import com.example.whatsinmyfridge.domain.model.Ingredient

data class PantryState(
    val ingredientInput: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val errorMessage: String? = null,
)

sealed interface PantryIntent {
    data class UpdateIngredientInput(val value: String) : PantryIntent
    data object AddIngredient : PantryIntent
    data class AddIngredients(val names: List<String>) : PantryIntent
    data class RemoveIngredient(val ingredient: Ingredient) : PantryIntent
    data object DismissError : PantryIntent
}
