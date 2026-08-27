package com.example.whatsinmyfridge.domain.repository

import com.example.whatsinmyfridge.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun observeProfile(): Flow<UserProfile?>
    suspend fun saveProfile(profile: UserProfile)
}
