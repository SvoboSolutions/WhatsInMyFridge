package com.example.whatsinmyfridge.domain.model

data class UserProfile(
    val uid: String,
    val displayName: String,
    val dietType: DietType = DietType.OMNIVORE,
    val allergies: Set<Allergy> = emptySet(),
    val onboardingCompleted: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
)
