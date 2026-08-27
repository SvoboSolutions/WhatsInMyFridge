package com.example.whatsinmyfridge.application.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsinmyfridge.domain.repository.User
import com.example.whatsinmyfridge.domain.usecase.ObserveCurrentUserUseCase
import com.example.whatsinmyfridge.domain.usecase.ObserveUserProfileUseCase
import com.example.whatsinmyfridge.domain.usecase.SignOutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val onboardingCompleted: Boolean = false,
) {
    val isLoggedIn: Boolean get() = user != null
    val needsOnboarding: Boolean get() = isLoggedIn && !onboardingCompleted
}

sealed interface AuthIntent {
    data object SignOut : AuthIntent
}

class AuthViewModel(
    observeCurrentUser: ObserveCurrentUserUseCase,
    observeUserProfile: ObserveUserProfileUseCase,
    private val signOut: SignOutUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(observeCurrentUser(), observeUserProfile()) { user, profile -> user to profile }
                .collect { (user, profile) ->
                    _state.value = AuthState(
                        isLoading = false,
                        user = user,
                        onboardingCompleted = profile?.onboardingCompleted == true,
                    )
                }
        }
    }

    fun onIntent(intent: AuthIntent) {
        when (intent) {
            AuthIntent.SignOut -> viewModelScope.launch { signOut() }
        }
    }
}
