package com.example.whatsinmyfridge.domain.repository

import com.example.whatsinmyfridge.domain.model.MealPlanEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface MealPlanRepository {
    fun observeEntries(): Flow<List<MealPlanEntry>>
    suspend fun setEntry(entry: MealPlanEntry)
    suspend fun removeEntry(date: LocalDate)
}
