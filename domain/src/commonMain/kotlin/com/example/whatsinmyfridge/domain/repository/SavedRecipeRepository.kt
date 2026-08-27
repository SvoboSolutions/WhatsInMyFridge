package com.example.whatsinmyfridge.domain.repository

import com.example.whatsinmyfridge.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface SavedRecipeRepository {
    fun observeSavedRecipes(): Flow<List<Recipe>>
    fun observeSavedRecipeIds(): Flow<Set<Long>>
    suspend fun saveRecipe(recipe: Recipe)
    suspend fun removeRecipe(recipeId: Long)
}
