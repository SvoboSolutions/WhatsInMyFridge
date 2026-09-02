package com.example.whatsinmyfridge.domain.model

/** Zutat mit Mengenangabe - erlaubt Skalieren auf eine andere Portionsanzahl. */
data class RecipeIngredient(
    val name: String,
    val amount: Double,
    val unit: String,
    val original: String,
)
