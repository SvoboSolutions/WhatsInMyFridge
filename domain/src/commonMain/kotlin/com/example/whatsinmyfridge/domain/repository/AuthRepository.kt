package com.example.whatsinmyfridge.domain.repository

import kotlinx.coroutines.flow.Flow

data class User(val uid: String, val displayName: String?, val email: String?)

/**
 * Beobachtet nur den Login-Status. Der eigentliche Sign-In-Flow (Google-Account-Auswahl)
 * ist zwingend an eine Compose-UI gebunden (native Auswahl-Dialoge) und läuft daher direkt
 * in presentation (siehe LoginScreen) statt über einen suspend-Repository-Aufruf hier.
 */
interface AuthRepository {
    fun observeCurrentUser(): Flow<User?>
    suspend fun signOut()
}
