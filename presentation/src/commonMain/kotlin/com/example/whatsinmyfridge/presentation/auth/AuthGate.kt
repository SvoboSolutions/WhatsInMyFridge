package com.example.whatsinmyfridge.presentation.auth

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.whatsinmyfridge.application.auth.AuthViewModel
import com.example.whatsinmyfridge.core.theme.FridgeMotion
import com.example.whatsinmyfridge.presentation.navigation.FridgeNavHost
import com.example.whatsinmyfridge.presentation.onboarding.OnboardingRoute
import org.koin.compose.viewmodel.koinViewModel

private enum class AuthGateStage { LOADING, LOGGED_OUT, NEEDS_ONBOARDING, LOGGED_IN }

/**
 * Wurzel-Composable: Login -> (falls nötig) Onboarding -> Haupt-App, mit Crossfade dazwischen.
 */
@Composable
fun AuthGate(viewModel: AuthViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    val stage = when {
        state.isLoading -> AuthGateStage.LOADING
        !state.isLoggedIn -> AuthGateStage.LOGGED_OUT
        state.needsOnboarding -> AuthGateStage.NEEDS_ONBOARDING
        else -> AuthGateStage.LOGGED_IN
    }

    Crossfade(targetState = stage, animationSpec = tween(FridgeMotion.DURATION_MEDIUM)) { current ->
        when (current) {
            AuthGateStage.LOADING -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            AuthGateStage.LOGGED_OUT -> LoginScreen()
            AuthGateStage.NEEDS_ONBOARDING -> OnboardingRoute(onCompleted = {})
            AuthGateStage.LOGGED_IN -> FridgeNavHost()
        }
    }
}
