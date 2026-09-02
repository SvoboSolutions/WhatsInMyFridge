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
 * Füllt alle noch leeren Tage der Auswahl auf einmal - ein Aufruf pro leerem Tag (Spoonacular
 * oder KI, je nach Profil-Einstellung, via SuggestRecipesForDayUseCase), bereits belegte Tage
 * werden nicht angetastet. Jeder neue Vorschlag schließt die Titel der bisher in diesem Lauf
 * vergebenen Rezepte aus, damit nicht mehrfach dasselbe Gericht landet.
 */
class GenerateWeeklyMealPlanUseCase(
    private val suggestRecipesForDay: SuggestRecipesForDayUseCase,
    private val mealPlanRepository: MealPlanRepository,
) {
    suspend operator fun invoke(dates: List<LocalDate>, existingEntries: Map<LocalDate, MealPlanEntry>): Result<Unit> {
        val emptyDates = dates.filterNot { it in existingEntries }
        if (emptyDates.isEmpty()) return Result.success(Unit)

        val usedTitles = mutableListOf<String>()
        for (date in emptyDates) {
            val recipe = suggestRecipesForDay(allowExtraIngredients = false, excludeTitles = usedTitles)
                .getOrElse { return Result.failure(it) }
                .firstOrNull()
                ?: return Result.failure(IllegalStateException("Keine passenden Rezepte gefunden"))

            usedTitles += recipe.title
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
