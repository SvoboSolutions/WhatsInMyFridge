package com.example.whatsinmyfridge.application.mealplan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsinmyfridge.core.logging.Logger
import com.example.whatsinmyfridge.domain.model.MealPlanEntry
import com.example.whatsinmyfridge.domain.model.Recipe
import com.example.whatsinmyfridge.domain.usecase.GenerateMealPlanSuggestionsUseCase
import com.example.whatsinmyfridge.domain.usecase.GenerateShoppingListUseCase
import com.example.whatsinmyfridge.domain.usecase.ObserveMealPlanUseCase
import com.example.whatsinmyfridge.domain.usecase.ObserveSavedRecipesUseCase
import com.example.whatsinmyfridge.domain.usecase.RemoveMealPlanEntryUseCase
import com.example.whatsinmyfridge.domain.usecase.SetMealPlanEntryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock

private const val TAG = "MealPlanViewModel"
private const val PLANNED_DAYS = 7

class MealPlanViewModel(
    private val observeMealPlan: ObserveMealPlanUseCase,
    private val observeSavedRecipes: ObserveSavedRecipesUseCase,
    private val generateMealPlanSuggestions: GenerateMealPlanSuggestionsUseCase,
    private val setMealPlanEntry: SetMealPlanEntryUseCase,
    private val removeMealPlanEntry: RemoveMealPlanEntryUseCase,
    private val generateShoppingList: GenerateShoppingListUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(MealPlanState())
    val state: StateFlow<MealPlanState> = _state.asStateFlow()

    init {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val weekDates = (0 until PLANNED_DAYS).map { offset -> today.plus(offset, DateTimeUnit.DAY) }
        _state.update { it.copy(weekDates = weekDates, selectedDates = weekDates.toSet()) }

        viewModelScope.launch {
            observeMealPlan().collect { entries ->
                _state.update { it.copy(entries = entries.associateBy(MealPlanEntry::date)) }
            }
        }
        viewModelScope.launch {
            observeSavedRecipes().collect { recipes ->
                _state.update { it.copy(savedRecipes = recipes) }
            }
        }
    }

    fun onIntent(intent: MealPlanIntent) {
        when (intent) {
            is MealPlanIntent.ToggleDay -> toggleDay(intent.date)
            MealPlanIntent.GenerateSuggestions -> generateSuggestions()
            is MealPlanIntent.OpenRecipePicker -> _state.update { it.copy(recipePickerForDate = intent.date) }
            MealPlanIntent.CloseRecipePicker -> _state.update { it.copy(recipePickerForDate = null) }
            is MealPlanIntent.AssignRecipe -> assignRecipe(intent.date, intent.recipe)
            is MealPlanIntent.RemoveEntry -> viewModelScope.launch { removeMealPlanEntry(intent.date) }
            MealPlanIntent.OpenShoppingList -> openShoppingList()
            MealPlanIntent.CloseShoppingList -> _state.update { it.copy(isShoppingListOpen = false) }
            is MealPlanIntent.ToggleShoppingItem -> toggleShoppingItem(intent.item)
            MealPlanIntent.DismissError -> _state.update { it.copy(errorMessage = null) }
            MealPlanIntent.ResetPlan -> resetPlan()
        }
    }

    private fun resetPlan() {
        val dates = _state.value.entries.keys.toList()
        viewModelScope.launch {
            dates.forEach { date -> removeMealPlanEntry(date) }
        }
    }

    private fun openShoppingList() {
        _state.update { it.copy(isShoppingListOpen = true, isShoppingListLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val entries = _state.value.weekDates.mapNotNull { date -> _state.value.entries[date] }
            generateShoppingList(entries)
                .onSuccess { shoppingList ->
                    _state.update { it.copy(isShoppingListLoading = false, shoppingList = shoppingList) }
                }
                .onFailure { error ->
                    Logger.e(TAG, "Einkaufsliste konnte nicht erstellt werden", error)
                    _state.update {
                        it.copy(isShoppingListLoading = false, errorMessage = error.message ?: "Unbekannter Fehler")
                    }
                }
        }
    }

    private fun toggleShoppingItem(item: String) {
        _state.update { current ->
            val checked = current.checkedShoppingItems
            current.copy(checkedShoppingItems = if (item in checked) checked - item else checked + item)
        }
    }

    private fun toggleDay(date: LocalDate) {
        _state.update { current ->
            val selected = current.selectedDates
            current.copy(selectedDates = if (date in selected) selected - date else selected + date)
        }
    }

    private fun generateSuggestions() {
        val dates = _state.value.selectedDates.toList()
        viewModelScope.launch {
            _state.update { it.copy(isGenerating = true, errorMessage = null) }
            generateMealPlanSuggestions(dates)
                .onFailure { error ->
                    Logger.e(TAG, "Essenplan-Vorschläge fehlgeschlagen", error)
                    _state.update { it.copy(errorMessage = error.message ?: "Unbekannter Fehler") }
                }
            _state.update { it.copy(isGenerating = false) }
        }
    }

    private fun assignRecipe(date: LocalDate, recipe: Recipe) {
        viewModelScope.launch {
            setMealPlanEntry(
                MealPlanEntry(
                    date = date,
                    recipeId = recipe.id,
                    recipeTitle = recipe.title,
                    recipeImageUrl = recipe.imageUrl,
                ),
            )
            _state.update { it.copy(recipePickerForDate = null) }
        }
    }
}
