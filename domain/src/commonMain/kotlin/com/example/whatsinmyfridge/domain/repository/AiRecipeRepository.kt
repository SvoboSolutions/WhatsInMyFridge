package com.example.whatsinmyfridge.domain.repository

import com.example.whatsinmyfridge.domain.model.Allergy
import com.example.whatsinmyfridge.domain.model.DietType
import com.example.whatsinmyfridge.domain.model.Ingredient
import com.example.whatsinmyfridge.domain.model.Recipe
import com.example.whatsinmyfridge.domain.model.RecipeDetails

interface AiRecipeRepository {
    suspend fun suggestRecipe(
        ingredients: List<Ingredient>,
        dietType: DietType,
        allergies: Set<Allergy>,
        allowExtraIngredients: Boolean,
        excludeTitles: List<String>,
    ): Result<Recipe>

    suspend fun getCachedDetails(recipeId: Long): RecipeDetails?
}
