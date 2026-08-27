package com.example.whatsinmyfridge.domain.repository

import com.example.whatsinmyfridge.domain.model.CookedRecipeEntry
import kotlinx.coroutines.flow.Flow

interface CookingLogRepository {
    fun observeCookedRecipes(): Flow<List<CookedRecipeEntry>>
    suspend fun logCookedRecipe(entry: CookedRecipeEntry)
}
