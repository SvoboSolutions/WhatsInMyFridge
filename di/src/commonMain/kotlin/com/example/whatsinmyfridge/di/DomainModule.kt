package com.example.whatsinmyfridge.di

import com.example.whatsinmyfridge.domain.usecase.AddPantryIngredientUseCase
import com.example.whatsinmyfridge.domain.usecase.GenerateShoppingListUseCase
import com.example.whatsinmyfridge.domain.usecase.GetRecipeDetailsUseCase
import com.example.whatsinmyfridge.domain.usecase.MarkRecipeAsCookedUseCase
import com.example.whatsinmyfridge.domain.usecase.ObserveCookedRecipesUseCase
import com.example.whatsinmyfridge.domain.usecase.ObserveCookingStatsUseCase
import com.example.whatsinmyfridge.domain.usecase.ObserveCurrentUserUseCase
import com.example.whatsinmyfridge.domain.usecase.ObserveMealPlanUseCase
import com.example.whatsinmyfridge.domain.usecase.ObservePantryUseCase
import com.example.whatsinmyfridge.domain.usecase.ObserveSavedRecipeIdsUseCase
import com.example.whatsinmyfridge.domain.usecase.ObserveSavedRecipesUseCase
import com.example.whatsinmyfridge.domain.usecase.ObserveUserProfileUseCase
import com.example.whatsinmyfridge.domain.usecase.RecognizeIngredientsFromPhotoUseCase
import com.example.whatsinmyfridge.domain.usecase.RemoveMealPlanEntryUseCase
import com.example.whatsinmyfridge.domain.usecase.RemovePantryIngredientUseCase
import com.example.whatsinmyfridge.domain.usecase.RemoveSavedRecipeUseCase
import com.example.whatsinmyfridge.domain.usecase.SaveRecipeUseCase
import com.example.whatsinmyfridge.domain.usecase.SaveUserProfileUseCase
import com.example.whatsinmyfridge.domain.usecase.SearchRecipesByIngredientsUseCase
import com.example.whatsinmyfridge.domain.usecase.SetMealPlanEntryUseCase
import com.example.whatsinmyfridge.domain.usecase.SignOutUseCase
import com.example.whatsinmyfridge.domain.usecase.SuggestRecipesForDayUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { SearchRecipesByIngredientsUseCase(get(), get(), get()) }
    factory { GetRecipeDetailsUseCase(get()) }
    factory { ObservePantryUseCase(get()) }
    factory { AddPantryIngredientUseCase(get()) }
    factory { RemovePantryIngredientUseCase(get()) }
    factory { ObserveCurrentUserUseCase(get()) }
    factory { SignOutUseCase(get()) }
    factory { ObserveSavedRecipesUseCase(get()) }
    factory { ObserveSavedRecipeIdsUseCase(get()) }
    factory { SaveRecipeUseCase(get()) }
    factory { RemoveSavedRecipeUseCase(get()) }
    factory { ObserveUserProfileUseCase(get()) }
    factory { SaveUserProfileUseCase(get()) }
    factory { MarkRecipeAsCookedUseCase(get()) }
    factory { ObserveCookedRecipesUseCase(get()) }
    factory { ObserveCookingStatsUseCase(get()) }
    factory { RecognizeIngredientsFromPhotoUseCase(get()) }
    factory { ObserveMealPlanUseCase(get()) }
    factory { SetMealPlanEntryUseCase(get()) }
    factory { RemoveMealPlanEntryUseCase(get()) }
    factory { SuggestRecipesForDayUseCase(get(), get(), get(), get()) }
    factory { GenerateShoppingListUseCase(get(), get()) }
}
