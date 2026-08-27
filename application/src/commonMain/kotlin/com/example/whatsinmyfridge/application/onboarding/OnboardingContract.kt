package com.example.whatsinmyfridge.application.onboarding

import com.example.whatsinmyfridge.domain.model.Allergy
import com.example.whatsinmyfridge.domain.model.DietType

enum class OnboardingStep {
    WELCOME,
    DIET,
    ALLERGIES,
    SUMMARY,
}

data class OnboardingState(
    val step: OnboardingStep = OnboardingStep.WELCOME,
    val displayName: String = "",
    val dietType: DietType = DietType.OMNIVORE,
    val allergies: Set<Allergy> = emptySet(),
    val isSaving: Boolean = false,
    val isCompleted: Boolean = false,
) {
    val canGoNext: Boolean get() = step != OnboardingStep.WELCOME || displayName.isNotBlank()
}

sealed interface OnboardingIntent {
    data class UpdateDisplayName(val value: String) : OnboardingIntent
    data class SelectDiet(val dietType: DietType) : OnboardingIntent
    data class ToggleAllergy(val allergy: Allergy) : OnboardingIntent
    data object NextStep : OnboardingIntent
    data object PreviousStep : OnboardingIntent
    data object Finish : OnboardingIntent
}
