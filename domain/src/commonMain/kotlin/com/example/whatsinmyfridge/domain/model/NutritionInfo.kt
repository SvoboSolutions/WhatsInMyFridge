package com.example.whatsinmyfridge.domain.model

/** Nährwerte pro Portion (so liefert Spoonacular sie in der Recipe-Information). */
data class NutritionInfo(
    val calories: Int?,
    val proteinG: Double?,
    val fatG: Double?,
    val carbsG: Double?,
)
