package com.example.whatsinmyfridge.infrastructure.remote

/**
 * Wird plattformspezifisch befüllt (Android: BuildConfig aus local.properties,
 * iOS: Info.plist aus Config.xcconfig/Secrets.xcconfig) und per Koin injiziert.
 * Kein Key liegt hartkodiert im Quellcode.
 */
data class ApiKeys(
    val spoonacularApiKey: String,
    val googleWebClientId: String,
    val anthropicApiKey: String,
)

const val SPOONACULAR_BASE_URL = "https://api.spoonacular.com"
const val ANTHROPIC_BASE_URL = "https://api.anthropic.com"
