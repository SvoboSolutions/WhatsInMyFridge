package com.example.whatsinmyfridge.di

import com.example.whatsinmyfridge.infrastructure.auth.initGoogleAuth
import com.example.whatsinmyfridge.infrastructure.remote.ApiKeys
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * Zentraler Einstiegspunkt. androidApp ruft initKoin(apiKeys) { androidContext(this) } auf,
 * iosApp (via :shared) ruft initKoin(apiKeys) ohne zusätzliche Konfiguration auf.
 * apiKeys kommt plattformspezifisch aus BuildConfig (Android) bzw. Info.plist (iOS) –
 * nirgends hartkodiert.
 */
fun initKoin(apiKeys: ApiKeys, config: KoinAppDeclaration? = null) {
    initGoogleAuth(apiKeys.googleWebClientId)
    startKoin {
        config?.invoke(this)
        modules(
            platformModule(),
            domainModule,
            infrastructureModule,
            applicationModule,
            module { single { apiKeys } },
        )
    }
}
