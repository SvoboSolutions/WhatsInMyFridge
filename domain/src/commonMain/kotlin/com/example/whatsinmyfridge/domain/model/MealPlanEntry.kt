package com.example.whatsinmyfridge.domain.model

import kotlinx.datetime.LocalDate

data class MealPlanEntry(
    val date: LocalDate,
    val recipeId: Long,
    val recipeTitle: String,
    val recipeImageUrl: String?,
)
