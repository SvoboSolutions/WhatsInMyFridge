package com.example.whatsinmyfridge.infrastructure.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ClaudeRecipeSuggestionPayloadDto(
    val title: String,
    val readyInMinutes: Int? = null,
    val servings: Int? = null,
    val summary: String = "",
    val ingredients: List<ClaudeRecipeIngredientDto> = emptyList(),
    val instructions: List<String> = emptyList(),
)

@Serializable
data class ClaudeRecipeIngredientDto(
    val name: String,
    val amount: Double = 0.0,
    val unit: String = "",
    val original: String,
)
