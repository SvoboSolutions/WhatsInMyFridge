package com.example.whatsinmyfridge

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.whatsinmyfridge.application.theme.ThemeViewModel
import com.example.whatsinmyfridge.core.theme.FridgeTheme
import com.example.whatsinmyfridge.domain.model.ThemeMode
import com.example.whatsinmyfridge.presentation.auth.AuthGate
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WhatsInMyFridgeApp(themeViewModel: ThemeViewModel = koinViewModel()) {
    val themeMode by themeViewModel.themeMode.collectAsState()
    val useDarkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    FridgeTheme(useDarkTheme = useDarkTheme) {
        AuthGate()
    }
}