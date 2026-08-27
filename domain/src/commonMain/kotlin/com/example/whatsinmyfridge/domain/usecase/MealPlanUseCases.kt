package com.example.whatsinmyfridge.domain.usecase

import com.example.whatsinmyfridge.domain.model.MealPlanEntry
import com.example.whatsinmyfridge.domain.repository.MealPlanRepository
import com.example.whatsinmyfridge.domain.repository.PantryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate

class ObserveMealPlanUseCase(private val mealPlanRepository: MealPlanRepository) {
    operator fun invoke(): Flow<List<MealPlanEntry>> = mealPlanRepository.observeEntries()
}

class SetMealPlanEntryUseCase(private val mealPlanRepository: MealPlanRepository) {
    suspend operator fun invoke(entry: MealPlanEntry) = mealPlanRepository.setEntry(entry)
}

class RemoveMealPlanEntryUseCase(private val mealPlanRepository: MealPlanRepository) {
    suspend operator fun invoke(date: LocalDate) = mealPlanRepository.removeEntry(date)
}

/**
 * Schlägt für jeden übergebenen Tag ein Rezept vor, basierend auf dem, was in der
 * Vorratskammer liegt. Überschreibt bestehende Einträge an diesen Tagen bewusst -
 * das ist der explizite "neu vorschlagen"-Button; einzelne Tage lassen sich danach
 * weiterhin separat manuell überschreiben.
 */
class GenerateMealPlanSuggestionsUseCase(
    private val pantryRepository: PantryRepository,
    private val searchRecipesByIngredients: SearchRecipesByIngredientsUseCase,
    private val mealPlanRepository: MealPlanRepository,
) {
    suspend operator fun invoke(dates: List<LocalDate>): Result<Unit> {
        if (dates.isEmpty()) return Result.success(Unit)

        val pantryIngredients = pantryRepository.observeSavedIngredients().first()
        if (pantryIngredients.isEmpty()) {
            return Result.failure(IllegalStateException("Vorratskammer ist leer - füge zuerst Zutaten hinzu"))
        }

        val recipes = searchRecipesByIngredients(pantryIngredients).getOrElse { return Result.failure(it) }
        if (recipes.isEmpty()) {
            return Result.failure(IllegalStateException("Keine passenden Rezepte für deine Vorratskammer gefunden"))
        }

        dates.forEachIndexed { index, date ->
            val recipe = recipes[index % recipes.size]
            mealPlanRepository.setEntry(
                MealPlanEntry(
                    date = date,
                    recipeId = recipe.id,
                    recipeTitle = recipe.title,
                    recipeImageUrl = recipe.imageUrl,
                ),
            )
        }
        return Result.success(Unit)
    }
}
