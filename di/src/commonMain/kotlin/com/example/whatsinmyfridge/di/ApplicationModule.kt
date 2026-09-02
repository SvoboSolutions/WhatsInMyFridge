package com.example.whatsinmyfridge.di

import com.example.whatsinmyfridge.application.auth.AuthViewModel
import com.example.whatsinmyfridge.application.detail.RecipeDetailViewModel
import com.example.whatsinmyfridge.application.mealplan.MealPlanViewModel
import com.example.whatsinmyfridge.application.onboarding.OnboardingViewModel
import com.example.whatsinmyfridge.application.pantry.PantryViewModel
import com.example.whatsinmyfridge.application.photo.PhotoIngredientViewModel
import com.example.whatsinmyfridge.application.profile.ProfileViewModel
import com.example.whatsinmyfridge.application.saved.SavedRecipesViewModel
import com.example.whatsinmyfridge.application.search.RecipeSearchViewModel
import com.example.whatsinmyfridge.application.theme.ThemeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val applicationModule = module {
    viewModelOf(::RecipeSearchViewModel)
    viewModelOf(::AuthViewModel)
    viewModelOf(::SavedRecipesViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::PhotoIngredientViewModel)
    viewModelOf(::PantryViewModel)
    viewModelOf(::MealPlanViewModel)
    viewModelOf(::ThemeViewModel)
    viewModel { params ->
        RecipeDetailViewModel(
            recipeId = params.get(),
            usedIngredientNames = params.get(),
            missedIngredientNames = params.get(),
            getRecipeDetails = get(),
            observeSavedRecipeIds = get(),
            saveRecipe = get(),
            removeSavedRecipe = get(),
            markRecipeAsCooked = get(),
        )
    }
}
