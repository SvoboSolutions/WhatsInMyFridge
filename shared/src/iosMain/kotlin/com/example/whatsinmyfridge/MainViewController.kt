package com.example.whatsinmyfridge

import androidx.compose.ui.window.ComposeUIViewController
import com.example.whatsinmyfridge.di.initKoin
import com.example.whatsinmyfridge.infrastructure.remote.ApiKeys
import platform.Foundation.NSBundle

private var koinStarted = false

private fun readApiKeys(): ApiKeys {
    val spoonacularApiKey = NSBundle.mainBundle
        .objectForInfoDictionaryKey("SpoonacularAPIKey") as? String
        ?: ""
    val googleWebClientId = NSBundle.mainBundle
        .objectForInfoDictionaryKey("GIDServerClientID") as? String
        ?: ""
    val anthropicApiKey = NSBundle.mainBundle
        .objectForInfoDictionaryKey("AnthropicAPIKey") as? String
        ?: ""
    return ApiKeys(
        spoonacularApiKey = spoonacularApiKey,
        googleWebClientId = googleWebClientId,
        anthropicApiKey = anthropicApiKey,
    )
}

fun MainViewController() = ComposeUIViewController {
    if (!koinStarted) {
        initKoin(apiKeys = readApiKeys())
        koinStarted = true
    }
    WhatsInMyFridgeApp()
}
