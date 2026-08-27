package com.example.whatsinmyfridge.infrastructure.auth

import com.example.whatsinmyfridge.domain.repository.AuthRepository
import com.example.whatsinmyfridge.domain.repository.User
import com.mmk.kmpauth.core.KMPAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl : AuthRepository {

    override fun observeCurrentUser(): Flow<User?> =
        KMPAuth.currentUserFlow.map { kmpAuthUser ->
            kmpAuthUser?.let {
                User(uid = it.uid, displayName = it.displayName, email = it.email)
            }
        }

    override suspend fun signOut() {
        KMPAuth.signOut()
    }
}
