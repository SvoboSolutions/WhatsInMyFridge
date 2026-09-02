package com.example.whatsinmyfridge.di

import com.example.whatsinmyfridge.domain.repository.AiRecipeRepository
import com.example.whatsinmyfridge.domain.repository.AuthRepository
import com.example.whatsinmyfridge.domain.repository.CookingLogRepository
import com.example.whatsinmyfridge.domain.repository.IngredientRecognitionRepository
import com.example.whatsinmyfridge.domain.repository.MealPlanRepository
import com.example.whatsinmyfridge.domain.repository.PantryRepository
import com.example.whatsinmyfridge.domain.repository.RecipeRepository
import com.example.whatsinmyfridge.domain.repository.SavedRecipeRepository
import com.example.whatsinmyfridge.domain.repository.UserProfileRepository
import com.example.whatsinmyfridge.infrastructure.auth.AuthRepositoryImpl
import com.example.whatsinmyfridge.infrastructure.local.AiRecipeRepositoryImpl
import com.example.whatsinmyfridge.infrastructure.local.CookingLogRepositoryImpl
import com.example.whatsinmyfridge.infrastructure.local.DatabaseDriverFactory
import com.example.whatsinmyfridge.infrastructure.local.FridgeDatabase
import com.example.whatsinmyfridge.infrastructure.local.MealPlanRepositoryImpl
import com.example.whatsinmyfridge.infrastructure.local.PantryRepositoryImpl
import com.example.whatsinmyfridge.infrastructure.local.SavedRecipeRepositoryImpl
import com.example.whatsinmyfridge.infrastructure.local.UserProfileRepositoryImpl
import com.example.whatsinmyfridge.infrastructure.remote.ApiKeys
import com.example.whatsinmyfridge.infrastructure.remote.ClaudeRecipeApi
import com.example.whatsinmyfridge.infrastructure.remote.ClaudeVisionApi
import com.example.whatsinmyfridge.infrastructure.remote.IngredientRecognitionRepositoryImpl
import com.example.whatsinmyfridge.infrastructure.remote.RecipeRepositoryImpl
import com.example.whatsinmyfridge.infrastructure.remote.SpoonacularApi
import com.example.whatsinmyfridge.infrastructure.remote.createHttpClient
import org.koin.dsl.module

val infrastructureModule = module {
    single { createHttpClient() }
    single { SpoonacularApi(httpClient = get(), apiKey = get<ApiKeys>().spoonacularApiKey) }
    single<RecipeRepository> { RecipeRepositoryImpl(get(), get()) }

    single { ClaudeVisionApi(httpClient = get(), apiKey = get<ApiKeys>().anthropicApiKey) }
    single<IngredientRecognitionRepository> { IngredientRecognitionRepositoryImpl(get()) }

    single { ClaudeRecipeApi(httpClient = get(), apiKey = get<ApiKeys>().anthropicApiKey) }
    single<AiRecipeRepository> { AiRecipeRepositoryImpl(get(), get()) }

    single { get<DatabaseDriverFactory>().createDriver() }
    single { FridgeDatabase(get()) }
    single<PantryRepository> { PantryRepositoryImpl(get()) }
    single<MealPlanRepository> { MealPlanRepositoryImpl(get()) }
    single<SavedRecipeRepository> { SavedRecipeRepositoryImpl(get()) }
    single<UserProfileRepository> { UserProfileRepositoryImpl(get()) }
    single<CookingLogRepository> { CookingLogRepositoryImpl(get()) }

    single<AuthRepository> { AuthRepositoryImpl() }
}
