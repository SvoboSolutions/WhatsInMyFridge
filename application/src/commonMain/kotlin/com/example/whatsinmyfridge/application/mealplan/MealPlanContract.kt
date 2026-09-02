package com.example.whatsinmyfridge.application.mealplan

import com.example.whatsinmyfridge.domain.model.MealPlanEntry
import com.example.whatsinmyfridge.domain.model.Recipe
import com.example.whatsinmyfridge.domain.model.ShoppingListEntry
import kotlinx.datetime.LocalDate

data class MealPlanState(
    val weekDates: List<LocalDate> = emptyList(),
    val selectedDates: Set<LocalDate> = emptySet(),
    val entries: Map<LocalDate, MealPlanEntry> = emptyMap(),
    val savedRecipes: List<Recipe> = emptyList(),
    val recipePickerForDate: LocalDate? = null,
    val suggestions: List<Recipe> = emptyList(),
    val isSuggesting: Boolean = false,
    val suggestionError: String? = null,
    val allowExtraIngredients: Boolean = false,
    val isGeneratingWeek: Boolean = false,
    val isShoppingListOpen: Boolean = false,
    val isShoppingListLoading: Boolean = false,
    val shoppingList: List<ShoppingListEntry> = emptyList(),
    val checkedShoppingItems: Set<String> = emptySet(),
    val errorMessage: String? = null,
) {
    val emptySelectedDayCount: Int get() = selectedDates.count { it !in entries }
    val canGenerateWeek: Boolean get() = !isGeneratingWeek && emptySelectedDayCount > 0
}

sealed interface MealPlanIntent {
    data class ToggleDay(val date: LocalDate) : MealPlanIntent
    data class OpenRecipePicker(val date: LocalDate) : MealPlanIntent
    data object CloseRecipePicker : MealPlanIntent
    data class AssignRecipe(val date: LocalDate, val recipe: Recipe) : MealPlanIntent
    data class RemoveEntry(val date: LocalDate) : MealPlanIntent
    data class SetAllowExtraIngredients(val allow: Boolean) : MealPlanIntent
    data object RequestSuggestions : MealPlanIntent
    data object GenerateWeeklyPlan : MealPlanIntent
    data object OpenShoppingList : MealPlanIntent
    data object CloseShoppingList : MealPlanIntent
    data class ToggleShoppingItem(val item: String) : MealPlanIntent
    data object DismissError : MealPlanIntent
    data object ResetPlan : MealPlanIntent
}
