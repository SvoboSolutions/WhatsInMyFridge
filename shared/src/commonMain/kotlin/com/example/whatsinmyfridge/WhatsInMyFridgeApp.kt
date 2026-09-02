package com.example.whatsinmyfridge

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.whatsinmyfridge.application.theme.ThemeViewModel
import com.example.whatsinmyfridge.core.theme.FridgeTheme
import com.example.whatsinmyfridge.presentation.auth.AuthGate
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WhatsInMyFridgeApp(themeViewModel: ThemeViewModel = koinViewModel()) {
    val themeMode by themeViewModel.themeMode.collectAsState()
    FridgeTheme(themeMode = themeMode) {
        AuthGate()
    }
}
