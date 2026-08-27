package com.example.whatsinmyfridge.domain.usecase

import com.example.whatsinmyfridge.domain.model.UserProfile
import com.example.whatsinmyfridge.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow

class ObserveUserProfileUseCase(private val repository: UserProfileRepository) {
    operator fun invoke(): Flow<UserProfile?> = repository.observeProfile()
}

class SaveUserProfileUseCase(private val repository: UserProfileRepository) {
    suspend operator fun invoke(profile: UserProfile) = repository.saveProfile(profile)
}
