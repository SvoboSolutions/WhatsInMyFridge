package com.example.whatsinmyfridge.domain.repository

import com.example.whatsinmyfridge.domain.model.Ingredient
import kotlinx.coroutines.flow.Flow

interface PantryRepository {
    fun observeSavedIngredients(): Flow<List<Ingredient>>
    suspend fun addIngredient(ingredient: Ingredient)
    suspend fun removeIngredient(ingredient: Ingredient)
}
