package com.example.whatsinmyfridge.presentation.pantry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.whatsinmyfridge.application.pantry.PantryIntent
import com.example.whatsinmyfridge.application.pantry.PantryState
import com.example.whatsinmyfridge.core.theme.FridgePillShape
import com.example.whatsinmyfridge.core.theme.FridgeSpacing

/**
 * Reine UI: bekommt fertigen State + sendet Intents. Zeigt alles, was dauerhaft in
 * Kühlschrank/Vorratskammer liegt - im Unterschied zur flüchtigen Zutatenliste bei der Suche.
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
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = FridgeSpacing.md)) {
            Text(
                "Was hast du dauerhaft zuhause?",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = FridgeSpacing.sm + FridgeSpacing.xs, bottom = FridgeSpacing.sm),
            )

            OutlinedTextField(
                value = state.ingredientInput,
                onValueChange = { onIntent(PantryIntent.UpdateIngredientInput(it)) },
                label = { Text("z.B. Reis") },
                trailingIcon = {
                    Row {
                        IconButton(onClick = onOpenPhotoRecognition) {
                            Icon(Icons.Filled.CameraAlt, contentDescription = "Per Foto erkennen")
                        }
                        IconButton(onClick = { onIntent(PantryIntent.AddIngredient) }) {
                            Icon(Icons.Filled.Add, contentDescription = "Hinzufügen")
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth(),
            )

            if (state.ingredients.isEmpty()) {
                EmptyPantryState()
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(FridgeSpacing.sm),
                    verticalArrangement = Arrangement.spacedBy(FridgeSpacing.sm),
                    modifier = Modifier.padding(top = FridgeSpacing.sm + FridgeSpacing.xs).fillMaxWidth(),
                ) {
                    state.ingredients.forEach { ingredient ->
                        FilterChip(
                            selected = true,
                            onClick = { onIntent(PantryIntent.RemoveIngredient(ingredient)) },
                            label = { Text(ingredient.name) },
                            shape = FridgePillShape,
                            trailingIcon = {
                                Icon(Icons.Filled.Close, contentDescription = "Entfernen", modifier = Modifier.padding(2.dp))
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyPantryState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = FridgeSpacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            Icons.Filled.Kitchen,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(56.dp).padding(bottom = FridgeSpacing.sm + FridgeSpacing.xs),
        )
        Text(
            "Noch leer",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            "Füge hinzu, was du dauerhaft zuhause hast - per Text oder Foto",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
        )
    }
}
