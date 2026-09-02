package com.example.whatsinmyfridge.domain.usecase

import com.example.whatsinmyfridge.domain.model.DietType
import com.example.whatsinmyfridge.domain.model.Recipe
import com.example.whatsinmyfridge.domain.model.RecipeSource
import com.example.whatsinmyfridge.domain.repository.AiRecipeRepository
import com.example.whatsinmyfridge.domain.repository.PantryRepository
import com.example.whatsinmyfridge.domain.repository.RecipeRepository
import com.example.whatsinmyfridge.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.first

/**
 * Liefert Rezeptvorschläge für EINEN Tag statt für die ganze Woche - wird beim Antippen
 * von "Vorschlag holen" im Essensplan aufgerufen, damit nie mehr angefragt wird als der
 * Nutzer gerade tatsächlich braucht. Die Quelle (Datenbank/KI) folgt der Profil-Einstellung.
 */
class SuggestRecipesForDayUseCase(
    private val pantryRepository: PantryRepository,
    private val userProfileRepository: UserProfileRepository,
    private val recipeRepository: RecipeRepository,
    private val aiRecipeRepository: AiRecipeRepository,
) {
    suspend operator fun invoke(allowExtraIngredients: Boolean, excludeTitles: List<String>): Result<List<Recipe>> {
        val pantryIngredients = pantryRepository.observeSavedIngredients().first()
        if (pantryIngredients.isEmpty()) {
            return Result.failure(IllegalStateException("Vorratskammer ist leer - füge zuerst Zutaten hinzu"))
        }

        val profile = userProfileRepository.observeProfile().first()
        val dietType = profile?.dietType ?: DietType.OMNIVORE
        val allergies = profile?.allergies ?: emptySet()

        return when (profile?.recipeSource ?: RecipeSource.DATABASE) {
            RecipeSource.DATABASE -> recipeRepository.findRecipesByIngredients(
                ingredients = pantryIngredients,
                dietType = dietType,
                allergies = allergies,
                maxMissingIngredients = if (allowExtraIngredients) 5 else 0,
                limit = 5,
            ).map { recipes -> recipes.filterNot { it.title in excludeTitles } }

            RecipeSource.AI -> aiRecipeRepository.suggestRecipe(
                ingredients = pantryIngredients,
                dietType = dietType,
                allergies = allergies,
                allowExtraIngredients = allowExtraIngredients,
                excludeTitles = excludeTitles,
            ).map { recipe -> listOf(recipe) }
        }
    }
}
