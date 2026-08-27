package com.example.whatsinmyfridge.domain.usecase

import com.example.whatsinmyfridge.domain.model.Recipe
import com.example.whatsinmyfridge.domain.repository.SavedRecipeRepository
import kotlinx.coroutines.flow.Flow

class ObserveSavedRecipesUseCase(private val savedRecipeRepository: SavedRecipeRepository) {
    operator fun invoke(): Flow<List<Recipe>> = savedRecipeRepository.observeSavedRecipes()
}

class ObserveSavedRecipeIdsUseCase(private val savedRecipeRepository: SavedRecipeRepository) {
    operator fun invoke(): Flow<Set<Long>> = savedRecipeRepository.observeSavedRecipeIds()
}

class SaveRecipeUseCase(private val savedRecipeRepository: SavedRecipeRepository) {
    suspend operator fun invoke(recipe: Recipe) = savedRecipeRepository.saveRecipe(recipe)
}

class RemoveSavedRecipeUseCase(private val savedRecipeRepository: SavedRecipeRepository) {
    suspend operator fun invoke(recipeId: Long) = savedRecipeRepository.removeRecipe(recipeId)
}
