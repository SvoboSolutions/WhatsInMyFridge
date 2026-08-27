package com.example.whatsinmyfridge.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface FridgeDestination {
    @Serializable
    data object RecipeSearch : FridgeDestination

    @Serializable
    data object Pantry : FridgeDestination

    @Serializable
    data object MealPlan : FridgeDestination

    @Serializable
    data object SavedRecipes : FridgeDestination

    @Serializable
    data object Profile : FridgeDestination

    @Serializable
    data object Settings : FridgeDestination

    @Serializable
    data class RecipeDetail(
        val recipeId: Long,
        val usedIngredientNames: List<String> = emptyList(),
        val missedIngredientNames: List<String> = emptyList(),
    ) : FridgeDestination
}

internal val bottomNavRoutes = setOf(
    FridgeDestination.RecipeSearch::class.qualifiedName,
    FridgeDestination.Pantry::class.qualifiedName,
    FridgeDestination.MealPlan::class.qualifiedName,
    FridgeDestination.SavedRecipes::class.qualifiedName,
    FridgeDestination.Profile::class.qualifiedName,
)
