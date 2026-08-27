package com.example.whatsinmyfridge

import android.app.Application
import com.example.whatsinmyfridge.di.initKoin
import com.example.whatsinmyfridge.infrastructure.remote.ApiKeys
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class WhatsInMyFridgeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(
            apiKeys = ApiKeys(
                spoonacularApiKey = BuildConfig.SPOONACULAR_API_KEY,
                googleWebClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID,
                anthropicApiKey = BuildConfig.ANTHROPIC_API_KEY,
            ),
        ) {
            androidLogger()
            androidContext(this@WhatsInMyFridgeApplication)
        }
    }
}
