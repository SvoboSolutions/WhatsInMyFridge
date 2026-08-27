package com.example.whatsinmyfridge.domain.usecase

import com.example.whatsinmyfridge.domain.model.CookedRecipeEntry
import com.example.whatsinmyfridge.domain.model.CookingStats
import com.example.whatsinmyfridge.domain.repository.CookingLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class MarkRecipeAsCookedUseCase(private val repository: CookingLogRepository) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(recipeId: Long, recipeTitle: String, usedIngredientCount: Int, missedIngredientCount: Int) {
        repository.logCookedRecipe(
            CookedRecipeEntry(
                recipeId = recipeId,
                recipeTitle = recipeTitle,
                usedIngredientCount = usedIngredientCount,
                missedIngredientCount = missedIngredientCount,
                cookedAt = Clock.System.now(),
            ),
        )
    }
}

class ObserveCookedRecipesUseCase(private val repository: CookingLogRepository) {
    operator fun invoke(): Flow<List<CookedRecipeEntry>> = repository.observeCookedRecipes()
}

class ObserveCookingStatsUseCase(private val repository: CookingLogRepository) {
    operator fun invoke(): Flow<CookingStats> = repository.observeCookedRecipes().map { entries ->
        CookingStats(
            totalScore = entries.sumOf { it.usedIngredientCount },
            recipesCookedCount = entries.size,
        )
    }
}
