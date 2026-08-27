package com.example.whatsinmyfridge.application.profile

import com.example.whatsinmyfridge.domain.model.CookingStats
import com.example.whatsinmyfridge.domain.model.UserProfile

data class ProfileState(
    val isLoading: Boolean = true,
    val profile: UserProfile? = null,
    val stats: CookingStats = CookingStats.EMPTY,
    val email: String? = null,
)

sealed interface ProfileIntent {
    data object SignOut : ProfileIntent
}
