package com.example.whatsinmyfridge.domain.model

data class Recipe(
    val id: Long,
    val title: String,
    val imageUrl: String?,
    val usedIngredients: List<Ingredient>,
    val missedIngredients: List<Ingredient>,
) {
    val missedIngredientCount: Int get() = missedIngredients.size
}
