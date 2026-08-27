package com.example.whatsinmyfridge

import androidx.compose.runtime.Composable
import com.example.whatsinmyfridge.core.theme.FridgeTheme
import com.example.whatsinmyfridge.presentation.auth.AuthGate

@Composable
fun WhatsInMyFridgeApp() {
    FridgeTheme {
        AuthGate()
    }
}
