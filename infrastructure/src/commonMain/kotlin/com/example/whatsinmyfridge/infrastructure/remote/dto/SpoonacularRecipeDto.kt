package com.example.whatsinmyfridge.infrastructure.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpoonacularRecipeDto(
    val id: Long,
    val title: String,
    val image: String? = null,
    @SerialName("usedIngredients") val usedIngredients: List<SpoonacularIngredientDto> = emptyList(),
    @SerialName("missedIngredients") val missedIngredients: List<SpoonacularIngredientDto> = emptyList(),
)

@Serializable
data class SpoonacularIngredientDto(
    val name: String,
)
