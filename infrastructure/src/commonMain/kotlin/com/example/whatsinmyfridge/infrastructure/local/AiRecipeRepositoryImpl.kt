package com.example.whatsinmyfridge.infrastructure.local

import com.example.whatsinmyfridge.domain.model.Allergy
import com.example.whatsinmyfridge.domain.model.DietType
import com.example.whatsinmyfridge.domain.model.Ingredient
import com.example.whatsinmyfridge.domain.model.Recipe
import com.example.whatsinmyfridge.domain.model.RecipeDetails
import com.example.whatsinmyfridge.domain.model.RecipeIngredient
import com.example.whatsinmyfridge.domain.repository.AiRecipeRepository
import com.example.whatsinmyfridge.infrastructure.remote.ClaudeRecipeApi
import com.example.whatsinmyfridge.infrastructure.remote.dto.ClaudeRecipeIngredientDto
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlin.random.Random

/**
 * Erzeugt Rezeptvorschläge per Claude statt Spoonacular. Ein KI-Rezept hat keine
 * externe ID - es bekommt eine synthetische NEGATIVE Long-ID (Spoonacular-IDs sind
 * immer positiv) und wird lokal gecacht, damit spätere getRecipeDetails-Aufrufe
 * (Rezeptdetails öffnen, Einkaufsliste) transparent funktionieren.
 */
class AiRecipeRepositoryImpl(
    private val api: ClaudeRecipeApi,
    private val database: FridgeDatabase,
) : AiRecipeRepository {

    override suspend fun suggestRecipe(
        ingredients: List<Ingredient>,
        dietType: DietType,
        allergies: Set<Allergy>,
        allowExtraIngredients: Boolean,
        excludeTitles: List<String>,
    ): Result<Recipe> {
        val payload = api.suggestRecipe(
            ingredientNames = ingredients.map { it.name },
            dietType = dietType.name,
            allergyNames = allergies.map { it.name },
            allowExtraIngredients = allowExtraIngredients,
            excludeTitles = excludeTitles,
        ).getOrElse { return Result.failure(it) }

        val id = -Random.nextLong(1, Long.MAX_VALUE)
        val pantryNames = ingredients.map { it.name.lowercase() }

        database.aiRecipeQueries.insertOrReplace(
            id = id,
            title = payload.title,
            readyInMinutes = payload.readyInMinutes?.toLong(),
            servings = payload.servings?.toLong(),
            summary = payload.summary,
            ingredientsJson = Json.encodeToString(ListSerializer(ClaudeRecipeIngredientDto.serializer()), payload.ingredients),
            instructionsJson = Json.encodeToString(ListSerializer(String.serializer()), payload.instructions),
        )

        val (used, missed) = payload.ingredients.partition { ingredient ->
            val normalized = ingredient.original.lowercase()
            pantryNames.any { pantryName -> normalized.contains(pantryName) }
        }

        return Result.success(
            Recipe(
                id = id,
                title = payload.title,
                imageUrl = null,
                usedIngredients = used.map { Ingredient(it.name) },
                missedIngredients = missed.map { Ingredient(it.name) },
            ),
        )
    }

    override suspend fun getCachedDetails(recipeId: Long): RecipeDetails? {
        val row = database.aiRecipeQueries.selectById(recipeId).executeAsOneOrNull() ?: return null
        val ingredients = Json.decodeFromString(ListSerializer(ClaudeRecipeIngredientDto.serializer()), row.ingredientsJson)
        val instructions = Json.decodeFromString(ListSerializer(String.serializer()), row.instructionsJson)

        return RecipeDetails(
            id = row.id,
            title = row.title,
            imageUrl = null,
            readyInMinutes = row.readyInMinutes?.toInt(),
            servings = row.servings?.toInt(),
            summary = row.summary,
            ingredients = ingredients.map { RecipeIngredient(name = it.name, amount = it.amount, unit = it.unit, original = it.original) },
            instructions = instructions,
            nutrition = null,
        )
    }
}
