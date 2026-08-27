package com.example.whatsinmyfridge.application.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsinmyfridge.domain.model.UserProfile
import com.example.whatsinmyfridge.domain.usecase.ObserveUserProfileUseCase
import com.example.whatsinmyfridge.domain.usecase.SaveUserProfileUseCase
import com.example.whatsinmyfridge.domain.usecase.SignOutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    observeUserProfile: ObserveUserProfileUseCase,
    private val saveUserProfile: SaveUserProfileUseCase,
    private val signOut: SignOutUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeUserProfile().collect { profile ->
                _state.update { it.copy(isLoading = false, profile = profile ?: it.profile) }
            }
        }
    }

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.SelectDiet -> persist { it.copy(dietType = intent.dietType) }
            is SettingsIntent.ToggleAllergy -> persist { it.copy(allergies = it.allergies.toggle(intent.allergy)) }
            SettingsIntent.SignOut -> viewModelScope.launch { signOut() }
        }
    }

    private fun persist(reducer: (UserProfile) -> UserProfile) {
        val current = _state.value.profile ?: return
        val updated = reducer(current)
        _state.update { it.copy(profile = updated) }
        viewModelScope.launch { saveUserProfile(updated) }
    }
}

private fun <T> Set<T>.toggle(item: T): Set<T> = if (item in this) this - item else this + item
