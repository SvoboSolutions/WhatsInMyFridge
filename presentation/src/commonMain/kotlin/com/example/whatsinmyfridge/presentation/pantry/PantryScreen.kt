package com.example.whatsinmyfridge.presentation.pantry

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.whatsinmyfridge.application.pantry.MAX_PANTRY_SIZE
import com.example.whatsinmyfridge.application.pantry.PantryIntent
import com.example.whatsinmyfridge.application.pantry.PantryState
import com.example.whatsinmyfridge.core.theme.FridgePillShape
import com.example.whatsinmyfridge.core.theme.FridgeSpacing
import com.example.whatsinmyfridge.domain.model.Ingredient
import com.example.whatsinmyfridge.domain.model.IngredientCategory
import com.example.whatsinmyfridge.domain.model.categorizeIngredient
import com.example.whatsinmyfridge.presentation.common.EmptyStateColumn
import com.example.whatsinmyfridge.presentation.common.ScreenHeaderRow

/**
 * Reine UI: bekommt fertigen State + sendet Intents. Zeigt alles, was dauerhaft in
 * Kühlschrank/Vorratskammer liegt - im Unterschied zur flüchtigen Zutatenliste bei der Suche.
 * Der gesamte Inhalt ist scrollbar, damit auch bei vielen Vorräten alles erreichbar bleibt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryScreen(
    state: PantryState,
    onIntent: (PantryIntent) -> Unit,
    onOpenPhotoRecognition: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            onIntent(PantryIntent.DismissError)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Vorratskammer") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = FridgeSpacing.md),
        ) {
            ScreenHeaderRow(
                icon = Icons.Filled.Kitchen,
                text = "Was hast du dauerhaft zuhause?",
                modifier = Modifier.padding(top = FridgeSpacing.smMd, bottom = FridgeSpacing.sm),
            )

            OutlinedTextField(
                value = state.ingredientInput,
                onValueChange = { onIntent(PantryIntent.UpdateIngredientInput(it)) },
                label = { Text(if (state.isFull) "Vorratskammer ist voll ($MAX_PANTRY_SIZE)" else "z.B. Reis") },
                enabled = !state.isFull,
                trailingIcon = {
                    Row {
                        IconButton(onClick = onOpenPhotoRecognition, enabled = !state.isFull) {
                            Icon(Icons.Filled.CameraAlt, contentDescription = "Per Foto erkennen")
                        }
                        IconButton(onClick = { onIntent(PantryIntent.AddIngredient) }, enabled = !state.isFull) {
                            Icon(Icons.Filled.Add, contentDescription = "Hinzufügen")
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth(),
            )

            if (state.ingredients.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = FridgeSpacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(FridgeSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "${state.ingredients.size} von $MAX_PANTRY_SIZE",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.weight(1f),
                    )

                    if (state.selectedForDeletion.isNotEmpty()) {
                        Button(
                            onClick = { onIntent(PantryIntent.DeleteSelected) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            ),
                            shape = FridgePillShape,
                            modifier = Modifier.height(40.dp),
                        ) {
                            Icon(Icons.Filled.DeleteOutline, contentDescription = null, modifier = Modifier.padding(end = FridgeSpacing.sm))
                            Text("${state.selectedForDeletion.size} löschen")
                        }
                        IconButton(onClick = { onIntent(PantryIntent.ClearSelection) }) {
                            Icon(Icons.Filled.Close, contentDescription = "Auswahl aufheben")
                        }
                    } else {
                        IconButton(onClick = { onIntent(PantryIntent.SelectAll) }) {
                            Icon(Icons.Filled.DoneAll, contentDescription = "Alle auswählen")
                        }
                    }
                }
            }

            if (state.ingredients.isEmpty()) {
                EmptyStateColumn(
                    icon = Icons.Filled.Kitchen,
                    title = "Noch leer",
                    subtitle = "Füge hinzu, was du dauerhaft zuhause hast - per Text oder Foto",
                )
            } else {
                val grouped = state.ingredients
                    .groupBy { categorizeIngredient(it.name) }

                IngredientCategory.entries.forEach { category ->
                    val items = grouped[category] ?: return@forEach
                    CategorySection(
                        category = category,
                        items = items,
                        selectedForDeletion = state.selectedForDeletion,
                        onToggle = { onIntent(PantryIntent.ToggleSelectForDeletion(it)) },
                    )
                }

                Box(modifier = Modifier.height(FridgeSpacing.lg))
            }
        }
    }
}

@Composable
private fun CategorySection(
    category: IngredientCategory,
    items: List<Ingredient>,
    selectedForDeletion: Set<Ingredient>,
    onToggle: (Ingredient) -> Unit,
) {
    val ui = category.ui

    Column(modifier = Modifier.padding(top = FridgeSpacing.md)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape).background(ui.color),
                contentAlignment = Alignment.Center,
            ) {
                Icon(ui.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
            Text(
                category.displayName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f).padding(horizontal = FridgeSpacing.sm),
            )
            Surface(
                shape = CircleShape,
                color = ui.color.copy(alpha = 0.15f),
            ) {
                Text(
                    "${items.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = ui.color,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = FridgeSpacing.sm, vertical = 2.dp),
                )
            }
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(FridgeSpacing.sm),
            verticalArrangement = Arrangement.spacedBy(FridgeSpacing.sm),
            modifier = Modifier.fillMaxWidth().padding(top = FridgeSpacing.sm).animateContentSize(),
        ) {
            items.forEach { ingredient ->
                val isMarked = ingredient in selectedForDeletion
                FilterChip(
                    selected = isMarked,
                    onClick = { onToggle(ingredient) },
                    label = { Text(ingredient.name.replaceFirstChar { it.titlecase() }) },
                    shape = FridgePillShape,
                    leadingIcon = if (isMarked) {
                        { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.padding(2.dp)) }
                    } else {
                        null
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = ui.color.copy(alpha = 0.12f),
                        labelColor = ui.color,
                        iconColor = ui.color,
                        selectedContainerColor = MaterialTheme.colorScheme.errorContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onErrorContainer,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.onErrorContainer,
                    ),
                    border = null,
                )
            }
        }
    }
}
