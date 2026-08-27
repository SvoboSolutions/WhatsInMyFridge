package com.example.whatsinmyfridge.domain.usecase

import com.example.whatsinmyfridge.domain.model.DietType
import com.example.whatsinmyfridge.domain.model.Ingredient
import com.example.whatsinmyfridge.domain.model.Recipe
import com.example.whatsinmyfridge.domain.repository.RecipeRepository
import com.example.whatsinmyfridge.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.first

/**
 * Berücksichtigt automatisch die Ernährungsweise/Unverträglichkeiten aus dem Profil,
 * ohne dass ViewModels das selbst verwalten müssen.
 */
class SearchRecipesByIngredientsUseCase(
    private val recipeRepository: RecipeRepository,
    private val userProfileRepository: UserProfileRepository,
) {
    suspend operator fun invoke(ingredients: List<Ingredient>): Result<List<Recipe>> {
        if (ingredients.isEmpty()) return Result.success(emptyList())
        val profile = userProfileRepository.observeProfile().first()
        return recipeRepository.findRecipesByIngredients(
            ingredients = ingredients,
            dietType = profile?.dietType ?: DietType.OMNIVORE,
            allergies = profile?.allergies ?: emptySet(),
        )
    }
}

class GetRecipeDetailsUseCase(
    private val recipeRepository: RecipeRepository,
) {
    suspend operator fun invoke(recipeId: Long) = recipeRepository.getRecipeDetails(recipeId)
}
