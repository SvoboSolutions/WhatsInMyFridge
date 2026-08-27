package com.example.whatsinmyfridge.infrastructure.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.whatsinmyfridge.domain.model.MealPlanEntry
import com.example.whatsinmyfridge.domain.repository.MealPlanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

class MealPlanRepositoryImpl(
    private val database: FridgeDatabase,
) : MealPlanRepository {

    override fun observeEntries(): Flow<List<MealPlanEntry>> =
        database.mealPlanQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows ->
                rows.map { row ->
                    MealPlanEntry(
                        date = LocalDate.parse(row.date),
                        recipeId = row.recipeId,
                        recipeTitle = row.recipeTitle,
                        recipeImageUrl = row.recipeImageUrl,
                    )
                }
            }

    override suspend fun setEntry(entry: MealPlanEntry) {
        database.mealPlanQueries.insertOrReplace(
            date = entry.date.toString(),
            recipeId = entry.recipeId,
            recipeTitle = entry.recipeTitle,
            recipeImageUrl = entry.recipeImageUrl,
        )
    }

    override suspend fun removeEntry(date: LocalDate) {
        database.mealPlanQueries.deleteByDate(date.toString())
    }
}
