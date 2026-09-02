package com.example.whatsinmyfridge.presentation.mealplan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.example.whatsinmyfridge.application.mealplan.MealPlanIntent
import com.example.whatsinmyfridge.application.mealplan.MealPlanState
import com.example.whatsinmyfridge.core.theme.FridgePillShape
import com.example.whatsinmyfridge.core.theme.FridgeSpacing
import com.example.whatsinmyfridge.domain.model.MealPlanEntry
import com.example.whatsinmyfridge.presentation.common.EmptyStateColumn
import com.example.whatsinmyfridge.presentation.common.ScreenHeaderRow
import kotlinx.datetime.LocalDate

/**
 * Reine UI: bekommt fertigen State + sendet Intents. Tage antippen um sie ein-/auszuschließen,
 * dann Vorschläge generieren - einzelne Tage bleiben danach separat manuell änderbar.
 *
 * Layout: eine "schwebende" Planungs-Karte oben (Tagesauswahl + Fortschritt + CTA) mit
 * spürbarem Schatten, darunter die Tagesliste - statt lose auf dem Hintergrund verstreuter
 * Elemente wie zuvor.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlanScreen(
    state: MealPlanState,
    onIntent: (MealPlanIntent) -> Unit,
    onRecipeClick: (MealPlanEntry) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showResetConfirmation by remember { mutableStateOf(false) }

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
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = FridgeSpacing.md, end = FridgeSpacing.md, bottom = FridgeSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(FridgeSpacing.md),
        ) {
            item {
                PlanningHeroCard(
                    state = state,
                    today = today,
                    plannedCount = plannedCount,
                    onIntent = onIntent,
                )
            }

            if (state.selectedDates.isEmpty()) {
                item {
                    EmptyStateColumn(
                        icon = Icons.Filled.CalendarMonth,
                        title = "Keine Tage ausgewählt",
                        subtitle = "Tippe oben auf die Tage, für die du planen möchtest",
                    )
                }
            } else {
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

            if (state.entries.isNotEmpty()) {
                item {
                    OutlinedButton(
                        onClick = { showResetConfirmation = true },
                        shape = FridgePillShape,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth().height(52.dp).padding(top = FridgeSpacing.sm),
                    ) {
                        Icon(Icons.Filled.RestartAlt, contentDescription = null, modifier = Modifier.padding(end = FridgeSpacing.sm))
                        Text("Plan zurücksetzen", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    if (showResetConfirmation) {
        AlertDialog(
            onDismissRequest = { showResetConfirmation = false },
            title = { Text("Plan zurücksetzen?") },
            text = { Text("Alle für diese Woche geplanten Rezepte werden entfernt. Die Tagesauswahl bleibt erhalten.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onIntent(MealPlanIntent.ResetPlan)
                        showResetConfirmation = false
                    },
                ) {
                    Text("Zurücksetzen", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmation = false }) {
                    Text("Abbrechen")
                }
            },
        )
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

@Composable
private fun PlanningHeroCard(
    state: MealPlanState,
    today: LocalDate?,
    plannedCount: Int,
    onIntent: (MealPlanIntent) -> Unit,
) {
    val shape = MaterialTheme.shapes.extraLarge
    Card(
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = shape,
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.28f),
            ),
    ) {
        Column(Modifier.padding(FridgeSpacing.lg)) {
            ScreenHeaderRow(icon = Icons.Filled.CalendarMonth, text = "Für welche Tage planen?")

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(FridgeSpacing.sm),
                modifier = Modifier.padding(top = FridgeSpacing.md),
            ) {
                items(state.weekDates) { date ->
                    DayPickerChip(
                        date = date,
                        isSelected = date in state.selectedDates,
                        hasEntry = date in state.entries,
                        isToday = date == today,
                        onClick = { onIntent(MealPlanIntent.ToggleDay(date)) },
                    )
                }
            }

            if (state.selectedDates.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = FridgeSpacing.lg)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            "Fortschritt",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            "$plannedCount / ${state.selectedDates.size}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    LinearProgressIndicator(
                        progress = { if (state.selectedDates.isEmpty()) 0f else plannedCount.toFloat() / state.selectedDates.size },
                        modifier = Modifier.fillMaxWidth().padding(top = FridgeSpacing.xs).height(8.dp).clip(FridgePillShape),
                        strokeCap = StrokeCap.Round,
                        trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    )
                }
            }

            GenerateSuggestionsButton(
                enabled = state.canGenerate,
                isGenerating = state.isGenerating,
                onClick = { onIntent(MealPlanIntent.GenerateSuggestions) },
                modifier = Modifier.padding(top = FridgeSpacing.lg),
            )
        }
    }
}

@Composable
private fun GenerateSuggestionsButton(
    enabled: Boolean,
    isGenerating: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = FridgePillShape
    val background = if (enabled) {
        Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))
    } else {
        SolidColor(MaterialTheme.colorScheme.surfaceContainerHighest)
    }
    val contentColor = if (enabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = if (enabled) 8.dp else 0.dp,
                shape = shape,
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
            )
            .clip(shape)
            .background(background)
            .clickable(enabled = enabled && !isGenerating, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (isGenerating) {
            CircularProgressIndicator(modifier = Modifier.size(22.dp), color = contentColor, strokeWidth = 2.5.dp)
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = contentColor)
                Text(
                    "Vorschläge generieren",
                    color = contentColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = FridgeSpacing.sm),
                )
            }
        }
    }
}

private val DayPickerChipSize = DpSize(56.dp, 72.dp)

@Composable
private fun DayPickerChip(
    date: LocalDate,
    isSelected: Boolean,
    hasEntry: Boolean,
    isToday: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(18.dp)
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val dotColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .size(DayPickerChipSize.width, DayPickerChipSize.height)
            .shadow(
                elevation = if (isSelected) 6.dp else 0.dp,
                shape = shape,
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
            )
            .clip(shape)
            .background(containerColor)
            .border(
                width = if (isToday && !isSelected) 1.5.dp else 0.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = shape,
            )
            .selectable(selected = isSelected, onClick = onClick, role = Role.Checkbox),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(FridgeSpacing.xs)) {
            Text(date.toWeekdayShort(), style = MaterialTheme.typography.labelSmall, color = contentColor, fontWeight = FontWeight.Medium)
            Text(
                date.toDayMonth().substringBefore("."),
                style = MaterialTheme.typography.titleMedium,
                color = contentColor,
                fontWeight = FontWeight.Bold,
            )
            Box(
                Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(if (hasEntry) dotColor else Color.Transparent),
            )
        }
    }
}
