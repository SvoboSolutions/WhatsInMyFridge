package com.example.whatsinmyfridge.application.search

import com.example.whatsinmyfridge.domain.model.Ingredient
import com.example.whatsinmyfridge.domain.model.Recipe

data class RecipeSearchState(
    val ingredientInput: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val isLoading: Boolean = false,
    val recipes: List<Recipe> = emptyList(),
    val hasSearched: Boolean = false,
    val savedRecipeIds: Set<Long> = emptySet(),
    val allowExtraIngredients: Boolean = true,
    val errorMessage: String? = null,
) {
    val canSearch: Boolean get() = ingredients.isNotEmpty() && !isLoading
}

sealed interface RecipeSearchIntent {
    data class UpdateIngredientInput(val value: String) : RecipeSearchIntent
    data object AddIngredient : RecipeSearchIntent
    data class AddIngredients(val names: List<String>) : RecipeSearchIntent
    data class RemoveIngredient(val ingredient: Ingredient) : RecipeSearchIntent
    data class SetAllowExtraIngredients(val allow: Boolean) : RecipeSearchIntent
    data object Search : RecipeSearchIntent
    data class ToggleSaveRecipe(val recipe: Recipe) : RecipeSearchIntent
    data object DismissError : RecipeSearchIntent
}
