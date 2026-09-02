package com.example.whatsinmyfridge.infrastructure.remote

import com.example.whatsinmyfridge.infrastructure.remote.dto.SpoonacularRecipeDetailsDto
import com.example.whatsinmyfridge.infrastructure.remote.dto.SpoonacularRecipeDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class SpoonacularApi(
    private val httpClient: HttpClient,
    private val apiKey: String,
) {
    /**
     * findByIngredients rankt nach möglichst wenigen fehlenden Zutaten und erlaubt Treffer,
     * die nicht alle Zutaten enthalten - anders als complexSearch/includeIngredients, das
     * *alle* angegebenen Zutaten im Rezept verlangt und bei 2-3 Zutaten schnell auf 0 Treffer läuft.
     */
    suspend fun findByIngredients(
        ingredientNames: List<String>,
        limit: Int,
    ): List<SpoonacularRecipeDto> =
        httpClient.get("$SPOONACULAR_BASE_URL/recipes/findByIngredients") {
            parameter("apiKey", apiKey)
            parameter("ingredients", ingredientNames.joinToString(","))
            parameter("number", limit)
            parameter("ranking", 1)
            parameter("ignorePantry", true)
        }.body()

    suspend fun getRecipeInformation(recipeId: Long): SpoonacularRecipeDetailsDto =
        httpClient.get("$SPOONACULAR_BASE_URL/recipes/$recipeId/information") {
            parameter("apiKey", apiKey)
            parameter("includeNutrition", true)
        }.body()

    /** Ein Call für mehrere Rezepte - für Diät-/Allergie-Nachfilterung ohne N+1-Requests. */
    suspend fun getRecipeInformationBulk(recipeIds: List<Long>): List<SpoonacularRecipeDetailsDto> {
        if (recipeIds.isEmpty()) return emptyList()
        return httpClient.get("$SPOONACULAR_BASE_URL/recipes/informationBulk") {
            parameter("apiKey", apiKey)
            parameter("ids", recipeIds.joinToString(","))
        }.body()
    }
}
