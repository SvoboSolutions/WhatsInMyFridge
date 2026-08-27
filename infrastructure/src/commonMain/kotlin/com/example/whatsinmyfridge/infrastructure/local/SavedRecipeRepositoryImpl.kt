package com.example.whatsinmyfridge.infrastructure.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.whatsinmyfridge.domain.model.Ingredient
import com.example.whatsinmyfridge.domain.model.Recipe
import com.example.whatsinmyfridge.domain.repository.SavedRecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val INGREDIENT_SEPARATOR = ""

class SavedRecipeRepositoryImpl(
    private val database: FridgeDatabase,
) : SavedRecipeRepository {

    override fun observeSavedRecipes(): Flow<List<Recipe>> =
        database.savedRecipeQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows ->
                rows.map { row ->
                    Recipe(
                        id = row.id,
                        title = row.title,
                        imageUrl = row.imageUrl,
                        usedIngredients = row.usedIngredients.toIngredientList(),
                        missedIngredients = row.missedIngredients.toIngredientList(),
                    )
                }
            }

    override fun observeSavedRecipeIds(): Flow<Set<Long>> =
        database.savedRecipeQueries.selectAllIds()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { it.toSet() }

    override suspend fun saveRecipe(recipe: Recipe) {
        database.savedRecipeQueries.insertOrReplace(
            id = recipe.id,
            title = recipe.title,
            imageUrl = recipe.imageUrl,
            usedIngredients = recipe.usedIngredients.joinToString(INGREDIENT_SEPARATOR) { it.name },
            missedIngredients = recipe.missedIngredients.joinToString(INGREDIENT_SEPARATOR) { it.name },
        )
    }

    override suspend fun removeRecipe(recipeId: Long) {
        database.savedRecipeQueries.deleteById(recipeId)
    }
}

private fun String.toIngredientList(): List<Ingredient> =
    if (isEmpty()) emptyList() else split(INGREDIENT_SEPARATOR).map { Ingredient(it) }
