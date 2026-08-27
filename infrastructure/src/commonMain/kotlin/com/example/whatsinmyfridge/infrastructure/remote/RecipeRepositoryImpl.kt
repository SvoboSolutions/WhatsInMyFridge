package com.example.whatsinmyfridge.infrastructure.remote

import com.example.whatsinmyfridge.core.logging.Logger
import com.example.whatsinmyfridge.domain.model.Allergy
import com.example.whatsinmyfridge.domain.model.DietType
import com.example.whatsinmyfridge.domain.model.Ingredient
import com.example.whatsinmyfridge.domain.model.Recipe
import com.example.whatsinmyfridge.domain.model.RecipeDetails
import com.example.whatsinmyfridge.domain.repository.RecipeRepository
import com.example.whatsinmyfridge.infrastructure.remote.dto.SpoonacularRecipeDetailsDto
import com.example.whatsinmyfridge.infrastructure.remote.dto.SpoonacularRecipeDto

class RecipeRepositoryImpl(
    private val api: SpoonacularApi,
) : RecipeRepository {

    override suspend fun findRecipesByIngredients(
        ingredients: List<Ingredient>,
        dietType: DietType,
        allergies: Set<Allergy>,
        maxMissingIngredients: Int,
        limit: Int,
    ): Result<List<Recipe>> = runCatching {
        val candidates = api.findByIngredients(
            ingredientNames = ingredients.map { it.name },
            limit = limit,
        ).map { it.toDomain() }.filter { it.missedIngredients.size <= maxMissingIngredients }

        if (dietType == DietType.OMNIVORE && allergies.isEmpty()) {
            return@runCatching candidates
        }

        val infoById = api.getRecipeInformationBulk(candidates.map { it.id }).associateBy { it.id }
        candidates.filter { recipe ->
            // Fail-open: Fehlt die Zusatzinfo, lieber anzeigen statt fälschlich ausblenden.
            val info = infoById[recipe.id] ?: return@filter true
            info.matchesDiet(dietType) && info.matchesAllergies(allergies)
        }
    }.onFailure { Logger.e("RecipeRepository", "findRecipesByIngredients failed", it) }

    override suspend fun getRecipeDetails(recipeId: Long): Result<RecipeDetails> = runCatching {
        api.getRecipeInformation(recipeId).toDomain()
    }.onFailure { Logger.e("RecipeRepository", "getRecipeDetails failed", it) }
}

private fun SpoonacularRecipeDto.toDomain(): Recipe = Recipe(
    id = id,
    title = title,
    imageUrl = image,
    usedIngredients = usedIngredients.map { Ingredient(it.name) },
    missedIngredients = missedIngredients.map { Ingredient(it.name) },
)

private val htmlTagRegex = Regex("<[^>]*>")

private fun SpoonacularRecipeDetailsDto.toDomain(): RecipeDetails = RecipeDetails(
    id = id,
    title = title,
    imageUrl = image,
    readyInMinutes = readyInMinutes,
    servings = servings,
    summary = summary.replace(htmlTagRegex, "").trim(),
    ingredients = extendedIngredients.map { it.original },
    instructions = analyzedInstructions.flatMap { group -> group.steps.map { it.step } },
)
