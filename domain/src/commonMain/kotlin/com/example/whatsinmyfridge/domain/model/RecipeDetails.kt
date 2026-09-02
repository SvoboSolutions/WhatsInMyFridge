package com.example.whatsinmyfridge.domain.model

data class RecipeDetails(
    val id: Long,
    val title: String,
    val imageUrl: String?,
    val readyInMinutes: Int?,
    val servings: Int?,
    val summary: String,
    val ingredients: List<RecipeIngredient>,
    val instructions: List<String>,
    val nutrition: NutritionInfo?,
)
