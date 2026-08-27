package com.example.whatsinmyfridge.domain.model

import kotlin.time.Instant

data class CookedRecipeEntry(
    val recipeId: Long,
    val recipeTitle: String,
    val usedIngredientCount: Int,
    val missedIngredientCount: Int,
    val cookedAt: Instant,
)

data class CookingStats(
    val totalScore: Int,
    val recipesCookedCount: Int,
) {
    companion object {
        val EMPTY = CookingStats(totalScore = 0, recipesCookedCount = 0)
    }
}
