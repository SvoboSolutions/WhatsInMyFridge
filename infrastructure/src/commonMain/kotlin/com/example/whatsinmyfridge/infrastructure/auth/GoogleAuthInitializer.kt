package com.example.whatsinmyfridge.infrastructure.auth

import com.mmk.kmpauth.core.KMPAuth
import com.mmk.kmpauth.google.google

/**
 * Einmalig beim App-Start aufrufen (siehe di/KoinInit.kt). Mit kmpauth-firebase in den
 * Dependencies registriert sich das Firebase-Backend automatisch – auf Android/iOS liest es
 * google-services.json/GoogleService-Info.plist, hier ist nur die Google-Web-Client-ID nötig.
 */
fun initGoogleAuth(webClientId: String) {
    KMPAuth.initialize {
        google(serverId = webClientId)
    }
}
