package com.example.whatsinmyfridge.infrastructure.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpoonacularRecipeDetailsDto(
    val id: Long,
    val title: String,
    val image: String? = null,
    val readyInMinutes: Int? = null,
    val servings: Int? = null,
    val summary: String = "",
    val vegetarian: Boolean = false,
    val vegan: Boolean = false,
    val glutenFree: Boolean = false,
    val dairyFree: Boolean = false,
    @SerialName("extendedIngredients") val extendedIngredients: List<SpoonacularExtendedIngredientDto> = emptyList(),
    @SerialName("analyzedInstructions") val analyzedInstructions: List<SpoonacularInstructionGroupDto> = emptyList(),
)

@Serializable
data class SpoonacularExtendedIngredientDto(
    val original: String,
)

@Serializable
data class SpoonacularInstructionGroupDto(
    val steps: List<SpoonacularInstructionStepDto> = emptyList(),
)

@Serializable
data class SpoonacularInstructionStepDto(
    val step: String,
)
