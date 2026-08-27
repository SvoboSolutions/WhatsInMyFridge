package com.example.whatsinmyfridge.application.mealplan

import com.example.whatsinmyfridge.domain.model.MealPlanEntry
import com.example.whatsinmyfridge.domain.model.Recipe
import kotlinx.datetime.LocalDate

data class MealPlanState(
    val weekDates: List<LocalDate> = emptyList(),
    val selectedDates: Set<LocalDate> = emptySet(),
    val entries: Map<LocalDate, MealPlanEntry> = emptyMap(),
    val savedRecipes: List<Recipe> = emptyList(),
    val isGenerating: Boolean = false,
    val recipePickerForDate: LocalDate? = null,
    val errorMessage: String? = null,
) {
    val canGenerate: Boolean get() = selectedDates.isNotEmpty() && !isGenerating
}

sealed interface MealPlanIntent {
    data class ToggleDay(val date: LocalDate) : MealPlanIntent
    data object GenerateSuggestions : MealPlanIntent
    data class OpenRecipePicker(val date: LocalDate) : MealPlanIntent
    data object CloseRecipePicker : MealPlanIntent
    data class AssignRecipe(val date: LocalDate, val recipe: Recipe) : MealPlanIntent
    data class RemoveEntry(val date: LocalDate) : MealPlanIntent
    data object DismissError : MealPlanIntent
}
