package com.example.whatsinmyfridge.domain.repository

import com.example.whatsinmyfridge.domain.model.Allergy
import com.example.whatsinmyfridge.domain.model.DietType
import com.example.whatsinmyfridge.domain.model.Ingredient
import com.example.whatsinmyfridge.domain.model.Recipe
import com.example.whatsinmyfridge.domain.model.RecipeDetails

interface RecipeRepository {
    suspend fun findRecipesByIngredients(
        ingredients: List<Ingredient>,
        dietType: DietType = DietType.OMNIVORE,
        allergies: Set<Allergy> = emptySet(),
        maxMissingIngredients: Int = 5,
        limit: Int = 20,
    ): Result<List<Recipe>>

    suspend fun getRecipeDetails(recipeId: Long): Result<RecipeDetails>
}
