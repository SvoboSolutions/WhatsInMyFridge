package com.example.whatsinmyfridge.domain.usecase

import com.example.whatsinmyfridge.domain.repository.AuthRepository
import com.example.whatsinmyfridge.domain.repository.User
import kotlinx.coroutines.flow.Flow

class ObserveCurrentUserUseCase(private val authRepository: AuthRepository) {
    operator fun invoke(): Flow<User?> = authRepository.observeCurrentUser()
}

class SignOutUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke() = authRepository.signOut()
}
