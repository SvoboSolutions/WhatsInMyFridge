package com.example.whatsinmyfridge.infrastructure.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.whatsinmyfridge.domain.model.Allergy
import com.example.whatsinmyfridge.domain.model.DietType
import com.example.whatsinmyfridge.domain.model.RecipeSource
import com.example.whatsinmyfridge.domain.model.ThemeMode
import com.example.whatsinmyfridge.domain.model.UserProfile
import com.example.whatsinmyfridge.domain.repository.UserProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val ALLERGY_SEPARATOR = "|"

class UserProfileRepositoryImpl(
    private val database: FridgeDatabase,
) : UserProfileRepository {

    override fun observeProfile(): Flow<UserProfile?> =
        database.userProfileQueries.selectFirst()
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { row ->
                row?.let {
                    UserProfile(
                        uid = it.uid,
                        displayName = it.displayName,
                        dietType = DietType.valueOf(it.dietType),
                        allergies = it.allergies.toAllergySet(),
                        onboardingCompleted = it.onboardingCompleted == 1L,
                        themeMode = ThemeMode.valueOf(it.themeMode),
                        recipeSource = RecipeSource.valueOf(it.recipeSource),
                    )
                }
            }

    override suspend fun saveProfile(profile: UserProfile) {
        database.userProfileQueries.insertOrReplace(
            uid = profile.uid,
            displayName = profile.displayName,
            dietType = profile.dietType.name,
            allergies = profile.allergies.joinToString(ALLERGY_SEPARATOR) { it.name },
            onboardingCompleted = if (profile.onboardingCompleted) 1L else 0L,
            themeMode = profile.themeMode.name,
            recipeSource = profile.recipeSource.name,
        )
    }
}

private fun String.toAllergySet(): Set<Allergy> =
    if (isEmpty()) emptySet() else split(ALLERGY_SEPARATOR).map { Allergy.valueOf(it) }.toSet()
