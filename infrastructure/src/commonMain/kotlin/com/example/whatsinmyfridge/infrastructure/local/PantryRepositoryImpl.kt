package com.example.whatsinmyfridge.infrastructure.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.whatsinmyfridge.domain.model.Ingredient
import com.example.whatsinmyfridge.domain.repository.PantryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PantryRepositoryImpl(
    private val database: FridgeDatabase,
) : PantryRepository {

    override fun observeSavedIngredients(): Flow<List<Ingredient>> =
        database.pantryQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { names -> names.map { Ingredient(it) } }

    override suspend fun addIngredient(ingredient: Ingredient) {
        database.pantryQueries.insertIngredient(ingredient.name)
    }

    override suspend fun removeIngredient(ingredient: Ingredient) {
        database.pantryQueries.deleteIngredient(ingredient.name)
    }
}
