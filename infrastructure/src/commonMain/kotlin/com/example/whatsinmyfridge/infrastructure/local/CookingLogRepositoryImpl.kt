package com.example.whatsinmyfridge.infrastructure.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.whatsinmyfridge.domain.model.CookedRecipeEntry
import com.example.whatsinmyfridge.domain.repository.CookingLogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Instant

class CookingLogRepositoryImpl(
    private val database: FridgeDatabase,
) : CookingLogRepository {

    override fun observeCookedRecipes(): Flow<List<CookedRecipeEntry>> =
        database.cookedRecipeQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows ->
                rows.map { row ->
                    CookedRecipeEntry(
                        recipeId = row.recipeId,
                        recipeTitle = row.recipeTitle,
                        usedIngredientCount = row.usedIngredientCount.toInt(),
                        missedIngredientCount = row.missedIngredientCount.toInt(),
                        cookedAt = Instant.fromEpochMilliseconds(row.cookedAtEpochMillis),
                    )
                }
            }

    override suspend fun logCookedRecipe(entry: CookedRecipeEntry) {
        database.cookedRecipeQueries.insertEntry(
            recipeId = entry.recipeId,
            recipeTitle = entry.recipeTitle,
            usedIngredientCount = entry.usedIngredientCount.toLong(),
            missedIngredientCount = entry.missedIngredientCount.toLong(),
            cookedAtEpochMillis = entry.cookedAt.toEpochMilliseconds(),
        )
    }
}
