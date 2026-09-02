package com.example.whatsinmyfridge.presentation.mealplan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.example.whatsinmyfridge.application.mealplan.MealPlanIntent
import com.example.whatsinmyfridge.application.mealplan.MealPlanState
import com.example.whatsinmyfridge.core.theme.FridgePillShape
import com.example.whatsinmyfridge.core.theme.FridgeSpacing
import com.example.whatsinmyfridge.domain.model.MealPlanEntry
import com.example.whatsinmyfridge.presentation.common.EmptyStateColumn
import com.example.whatsinmyfridge.presentation.common.ScreenHeaderRow

/**
 * Reine UI: bekommt fertigen State + sendet Intents. Tage antippen um sie ein-/auszuschließen,
 * dann Vorschläge generieren - einzelne Tage bleiben danach separat manuell änderbar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlanScreen(
    state: MealPlanState,
    onIntent: (MealPlanIntent) -> Unit,
    onRecipeClick: (MealPlanEntry) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            onIntent(MealPlanIntent.DismissError)
        }
    }

    val today = state.weekDates.firstOrNull()
    val plannedCount = state.selectedDates.count { it in state.entries }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Essenplan") },
                actions = {
                    IconButton(onClick = { onIntent(MealPlanIntent.OpenShoppingList) }) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = "Einkaufsliste")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            ScreenHeaderRow(
                icon = Icons.Filled.CalendarMonth,
                text = "Für welche Tage planen?",
                modifier = Modifier.padding(horizontal = FridgeSpacing.md, vertical = FridgeSpacing.sm),
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(FridgeSpacing.sm),
                contentPadding = PaddingValues(horizontal = FridgeSpacing.md),
            ) {
                items(state.weekDates) { date ->
                    val hasEntry = date in state.entries
                    FilterChip(
                        selected = date in state.selectedDates,
                        onClick = { onIntent(MealPlanIntent.ToggleDay(date)) },
                        label = { Text("${date.toWeekdayShort()} ${date.toDayMonth()}") },
                        shape = FridgePillShape,
                        trailingIcon = if (hasEntry) {
                            {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(6.dp),
                                ) {}
                            }
                        } else {
                            null
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedTrailingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        ),
                    )
                }
            }

            if (state.selectedDates.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = FridgeSpacing.md, vertical = FridgeSpacing.sm)) {
                    Text(
                        "$plannedCount von ${state.selectedDates.size} Tagen geplant",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    LinearProgressIndicator(
                        progress = { if (state.selectedDates.isEmpty()) 0f else plannedCount.toFloat() / state.selectedDates.size },
                        modifier = Modifier.fillMaxWidth().padding(top = FridgeSpacing.xs).height(6.dp),
                        strokeCap = StrokeCap.Round,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }

            Button(
                onClick = { onIntent(MealPlanIntent.GenerateSuggestions) },
                enabled = state.canGenerate,
                shape = FridgePillShape,
                modifier = Modifier.padding(horizontal = FridgeSpacing.md, vertical = FridgeSpacing.sm).fillMaxWidth().height(52.dp),
            ) {
                if (state.isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.height(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.padding(end = FridgeSpacing.sm))
                    Text("Vorschläge generieren")
                }
            }

            if (state.selectedDates.isEmpty()) {
                EmptyStateColumn(
                    icon = Icons.Filled.CalendarMonth,
                    title = "Keine Tage ausgewählt",
                    subtitle = "Tippe oben auf die Tage, für die du planen möchtest",
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = FridgeSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(FridgeSpacing.sm),
                    contentPadding = PaddingValues(top = FridgeSpacing.xs, bottom = FridgeSpacing.md),
                ) {
                    items(state.weekDates.filter { it in state.selectedDates }, key = { it.toString() }) { date ->
                        MealPlanDayCard(
                            date = date,
                            entry = state.entries[date],
                            isToday = date == today,
                            onPickRecipe = { onIntent(MealPlanIntent.OpenRecipePicker(date)) },
                            onRemoveEntry = { onIntent(MealPlanIntent.RemoveEntry(date)) },
                            onOpenDetails = { state.entries[date]?.let(onRecipeClick) },
                            modifier = Modifier.animateItem(),
                        )
                    }
                }
            }
        }
    }

    state.recipePickerForDate?.let { date ->
        RecipePickerSheet(
            savedRecipes = state.savedRecipes,
            onPick = { recipe -> onIntent(MealPlanIntent.AssignRecipe(date, recipe)) },
            onDismiss = { onIntent(MealPlanIntent.CloseRecipePicker) },
        )
    }

    if (state.isShoppingListOpen) {
        ShoppingListSheet(
            isLoading = state.isShoppingListLoading,
            entries = state.shoppingList,
            checkedItems = state.checkedShoppingItems,
            onToggleItem = { onIntent(MealPlanIntent.ToggleShoppingItem(it)) },
            onDismiss = { onIntent(MealPlanIntent.CloseShoppingList) },
        )
    }
}
