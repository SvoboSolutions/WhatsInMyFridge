package com.example.whatsinmyfridge.application.settings

import com.example.whatsinmyfridge.domain.model.Allergy
import com.example.whatsinmyfridge.domain.model.DietType
import com.example.whatsinmyfridge.domain.model.UserProfile

data class SettingsState(
    val isLoading: Boolean = true,
    val profile: UserProfile? = null,
)

sealed interface SettingsIntent {
    data class SelectDiet(val dietType: DietType) : SettingsIntent
    data class ToggleAllergy(val allergy: Allergy) : SettingsIntent
    data object SignOut : SettingsIntent
}
