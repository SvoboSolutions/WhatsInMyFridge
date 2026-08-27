package com.example.whatsinmyfridge.presentation.mealplan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.whatsinmyfridge.application.mealplan.MealPlanIntent
import com.example.whatsinmyfridge.application.mealplan.MealPlanState
import com.example.whatsinmyfridge.core.theme.FridgePillShape
import com.example.whatsinmyfridge.core.theme.FridgeSpacing

/**
 * Reine UI: bekommt fertigen State + sendet Intents. Tage antippen um sie ein-/auszuschließen,
 * dann Vorschläge generieren - einzelne Tage bleiben danach separat manuell änderbar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlanScreen(
    state: MealPlanState,
    onIntent: (MealPlanIntent) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            onIntent(MealPlanIntent.DismissError)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Essenplan") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Text(
                "Für welche Tage planen?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = FridgeSpacing.md, vertical = FridgeSpacing.sm),
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(FridgeSpacing.sm),
                contentPadding = PaddingValues(horizontal = FridgeSpacing.md),
            ) {
                items(state.weekDates) { date ->
                    FilterChip(
                        selected = date in state.selectedDates,
                        onClick = { onIntent(MealPlanIntent.ToggleDay(date)) },
                        label = { Text("${date.toWeekdayShort()} ${date.toDayMonth()}") },
                        shape = FridgePillShape,
                    )
                }
            }

            Button(
                onClick = { onIntent(MealPlanIntent.GenerateSuggestions) },
                enabled = state.canGenerate,
                shape = FridgePillShape,
                modifier = Modifier.padding(horizontal = FridgeSpacing.md, vertical = FridgeSpacing.md).fillMaxWidth().height(52.dp),
            ) {
                if (state.isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.height(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.padding(end = FridgeSpacing.sm))
                    Text("Vorschläge generieren")
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = FridgeSpacing.md),
                verticalArrangement = Arrangement.spacedBy(FridgeSpacing.sm),
                contentPadding = PaddingValues(bottom = FridgeSpacing.md),
            ) {
                items(state.weekDates.filter { it in state.selectedDates }, key = { it.toString() }) { date ->
                    MealPlanDayCard(
                        date = date,
                        entry = state.entries[date],
                        onPickRecipe = { onIntent(MealPlanIntent.OpenRecipePicker(date)) },
                        onRemoveEntry = { onIntent(MealPlanIntent.RemoveEntry(date)) },
                    )
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
}
