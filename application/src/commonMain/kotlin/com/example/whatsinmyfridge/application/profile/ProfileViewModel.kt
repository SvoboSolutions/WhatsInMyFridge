package com.example.whatsinmyfridge.application.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsinmyfridge.domain.usecase.ObserveCookingStatsUseCase
import com.example.whatsinmyfridge.domain.usecase.ObserveCurrentUserUseCase
import com.example.whatsinmyfridge.domain.usecase.ObserveUserProfileUseCase
import com.example.whatsinmyfridge.domain.usecase.SaveUserProfileUseCase
import com.example.whatsinmyfridge.domain.usecase.SignOutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    observeUserProfile: ObserveUserProfileUseCase,
    observeCookingStats: ObserveCookingStatsUseCase,
    observeCurrentUser: ObserveCurrentUserUseCase,
    private val saveUserProfile: SaveUserProfileUseCase,
    private val signOut: SignOutUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(observeUserProfile(), observeCookingStats(), observeCurrentUser()) { profile, stats, user ->
                Triple(profile, stats, user?.email)
            }.collect { (profile, stats, email) ->
                _state.update { it.copy(isLoading = false, profile = profile, stats = stats, email = email) }
            }
        }
    }

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.SetThemeMode -> {
                val current = _state.value.profile ?: return
                val updated = current.copy(themeMode = intent.themeMode)
                _state.update { it.copy(profile = updated) }
                viewModelScope.launch { saveUserProfile(updated) }
            }
            ProfileIntent.SignOut -> viewModelScope.launch { signOut() }
        }
    }
}