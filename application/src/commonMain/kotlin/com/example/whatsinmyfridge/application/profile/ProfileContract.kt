package com.example.whatsinmyfridge.application.profile

import com.example.whatsinmyfridge.domain.model.Allergy
import com.example.whatsinmyfridge.domain.model.CookingStats
import com.example.whatsinmyfridge.domain.model.DietType
import com.example.whatsinmyfridge.domain.model.RecipeSource
import com.example.whatsinmyfridge.domain.model.ThemeMode
import com.example.whatsinmyfridge.domain.model.UserProfile

data class ProfileState(
    val isLoading: Boolean = true,
    val profile: UserProfile? = null,
    val stats: CookingStats = CookingStats.EMPTY,
    val email: String? = null,
)

sealed interface ProfileIntent {
    data class SelectDiet(val dietType: DietType) : ProfileIntent
    data class ToggleAllergy(val allergy: Allergy) : ProfileIntent
    data class SetThemeMode(val themeMode: ThemeMode) : ProfileIntent
    data class SetRecipeSource(val recipeSource: RecipeSource) : ProfileIntent
    data object SignOut : ProfileIntent
}
