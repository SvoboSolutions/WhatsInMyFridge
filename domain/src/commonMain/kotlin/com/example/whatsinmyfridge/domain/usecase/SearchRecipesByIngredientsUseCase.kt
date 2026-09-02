package com.example.whatsinmyfridge.domain.usecase

import com.example.whatsinmyfridge.domain.model.DietType
import com.example.whatsinmyfridge.domain.model.Ingredient
import com.example.whatsinmyfridge.domain.model.Recipe
import com.example.whatsinmyfridge.domain.model.RecipeSource
import com.example.whatsinmyfridge.domain.repository.AiRecipeRepository
import com.example.whatsinmyfridge.domain.repository.RecipeRepository
import com.example.whatsinmyfridge.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.first

/**
 * Berücksichtigt automatisch die Ernährungsweise/Unverträglichkeiten aus dem Profil,
 * ohne dass ViewModels das selbst verwalten müssen. Folgt außerdem der Rezeptquelle
 * (Datenbank/KI) aus dem Profil - im KI-Modus wird Spoonacular übersprungen und
 * stattdessen genau ein von Claude vorgeschlagenes Rezept zurückgegeben.
 */
class SearchRecipesByIngredientsUseCase(
    private val recipeRepository: RecipeRepository,
    private val aiRecipeRepository: AiRecipeRepository,
    private val userProfileRepository: UserProfileRepository,
) {
    suspend operator fun invoke(ingredients: List<Ingredient>): Result<List<Recipe>> {
        if (ingredients.isEmpty()) return Result.success(emptyList())
        val profile = userProfileRepository.observeProfile().first()
        val dietType = profile?.dietType ?: DietType.OMNIVORE
        val allergies = profile?.allergies ?: emptySet()

        return when (profile?.recipeSource ?: RecipeSource.DATABASE) {
            RecipeSource.DATABASE -> recipeRepository.findRecipesByIngredients(
                ingredients = ingredients,
                dietType = dietType,
                allergies = allergies,
            )

            RecipeSource.AI -> aiRecipeRepository.suggestRecipe(
                ingredients = ingredients,
                dietType = dietType,
                allergies = allergies,
                allowExtraIngredients = true,
                excludeTitles = emptyList(),
            ).map { recipe -> listOf(recipe) }
        }
    }
}

class GetRecipeDetailsUseCase(
    private val recipeRepository: RecipeRepository,
) {
    suspend operator fun invoke(recipeId: Long) = recipeRepository.getRecipeDetails(recipeId)
}
