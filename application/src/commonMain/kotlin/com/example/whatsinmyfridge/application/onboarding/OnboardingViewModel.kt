package com.example.whatsinmyfridge.application.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsinmyfridge.domain.model.UserProfile
import com.example.whatsinmyfridge.domain.usecase.ObserveCurrentUserUseCase
import com.example.whatsinmyfridge.domain.usecase.SaveUserProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val stepOrder = OnboardingStep.entries

class OnboardingViewModel(
    private val observeCurrentUser: ObserveCurrentUserUseCase,
    private val saveUserProfile: SaveUserProfileUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    private var currentUid: String? = null

    init {
        viewModelScope.launch {
            val user = observeCurrentUser().first()
            currentUid = user?.uid
            val name = user?.displayName
            if (!name.isNullOrBlank()) {
                _state.update { it.copy(displayName = name) }
            }
        }
    }

    fun onIntent(intent: OnboardingIntent) {
        when (intent) {
            is OnboardingIntent.UpdateDisplayName -> _state.update { it.copy(displayName = intent.value) }
            is OnboardingIntent.SelectDiet -> _state.update { it.copy(dietType = intent.dietType) }
            is OnboardingIntent.ToggleAllergy -> _state.update {
                it.copy(allergies = it.allergies.toggle(intent.allergy))
            }
            OnboardingIntent.NextStep -> moveStep(1)
            OnboardingIntent.PreviousStep -> moveStep(-1)
            OnboardingIntent.Finish -> finish()
        }
    }

    private fun moveStep(delta: Int) {
        val currentIndex = stepOrder.indexOf(_state.value.step)
        val nextIndex = (currentIndex + delta).coerceIn(0, stepOrder.lastIndex)
        _state.update { it.copy(step = stepOrder[nextIndex]) }
    }

    private fun finish() {
        val uid = currentUid ?: return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val current = _state.value
            saveUserProfile(
                UserProfile(
                    uid = uid,
                    displayName = current.displayName,
                    dietType = current.dietType,
                    allergies = current.allergies,
                    onboardingCompleted = true,
                ),
            )
            _state.update { it.copy(isSaving = false, isCompleted = true) }
        }
    }
}

private fun <T> Set<T>.toggle(item: T): Set<T> = if (item in this) this - item else this + item
