package com.example.whatsinmyfridge.domain.usecase

import com.example.whatsinmyfridge.domain.model.MealPlanEntry
import com.example.whatsinmyfridge.domain.model.ShoppingListEntry
import com.example.whatsinmyfridge.domain.repository.MealPlanRepository
import com.example.whatsinmyfridge.domain.repository.PantryRepository
import com.example.whatsinmyfridge.domain.repository.RecipeRepository
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
 * Holt für jedes geplante Rezept die vollständige Zutatenliste und zieht ab, was bereits in
 * der Vorratskammer liegt (Substring-Abgleich am Zutatentext, wie schon bei der Allergie-
 * Filterung - kein exaktes Matching nötig, "Hauptsache nichts Doppeltes auf dem Zettel").
 */
class GenerateShoppingListUseCase(
    private val recipeRepository: RecipeRepository,
    private val pantryRepository: PantryRepository,
) {
    suspend operator fun invoke(entries: List<MealPlanEntry>): Result<List<ShoppingListEntry>> = runCatching {
        val pantryNames = pantryRepository.observeSavedIngredients().first().map { it.name.lowercase() }

        entries.sortedBy { it.date }.mapNotNull { entry ->
            val details = recipeRepository.getRecipeDetails(entry.recipeId).getOrNull() ?: return@mapNotNull null
            val missing = details.ingredients.filter { ingredient ->
                val normalized = ingredient.original.lowercase()
                pantryNames.none { pantryName -> normalized.contains(pantryName) }
            }.map { it.original }
            if (missing.isEmpty()) null else ShoppingListEntry(entry.date, entry.recipeTitle, missing)
        }
    }
}
