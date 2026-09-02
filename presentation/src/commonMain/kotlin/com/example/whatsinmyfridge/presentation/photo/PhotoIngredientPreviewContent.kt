package com.example.whatsinmyfridge.presentation.photo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.whatsinmyfridge.application.photo.PhotoIngredientIntent
import com.example.whatsinmyfridge.application.photo.PhotoIngredientState
import com.example.whatsinmyfridge.core.theme.FridgePillShape
import com.example.whatsinmyfridge.core.theme.FridgeSpacing

/**
 * Vorschau der von der KI erkannten Zutaten. Nutzer:in kann einzelne Vorschläge entfernen
 * und manuell fehlende Zutaten ergänzen, bevor die Liste in die Suche übernommen wird.
 *
 * Der gesamte Inhalt ist scrollbar, damit "Manuell hinzufügen" und "Übernehmen" auch bei
 * vielen erkannten Zutaten immer erreichbar bleiben.
 */
@Composable
fun PhotoIngredientPreviewContent(
    state: PhotoIngredientState,
    onIntent: (PhotoIngredientIntent) -> Unit,
    onAddAnotherPhoto: () -> Unit,
    onConfirm: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = FridgeSpacing.md),
    ) {
        Text(
            "Erkannte Zutaten",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = FridgeSpacing.md, bottom = FridgeSpacing.sm),
        )

        if (state.confirmedIngredients.isEmpty() && state.suggestedIngredients.isEmpty()) {
            Column(modifier = Modifier.padding(vertical = FridgeSpacing.lg)) {
                Icon(Icons.Filled.SearchOff, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
                Text(
                    "Keine Zutaten erkannt. Du kannst welche manuell ergänzen.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = FridgeSpacing.sm),
                )
            }
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(FridgeSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(FridgeSpacing.sm),
                modifier = Modifier.fillMaxWidth(),
            ) {
                state.confirmedIngredients.forEach { name ->
                    FilterChip(
                        selected = true,
                        onClick = { onIntent(PhotoIngredientIntent.RemoveIngredient(name)) },
                        label = { Text(name.replaceFirstChar { it.titlecase() }) },
                        shape = FridgePillShape,
                        trailingIcon = {
                            Icon(Icons.Filled.Close, contentDescription = "Entfernen", modifier = Modifier.padding(2.dp))
                        },
                    )
                }
            }

            if (state.suggestedIngredients.isNotEmpty()) {
                Text(
                    "Nicht ganz sicher - trotzdem übernehmen?",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = FridgeSpacing.md, bottom = FridgeSpacing.sm),
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(FridgeSpacing.sm),
                    verticalArrangement = Arrangement.spacedBy(FridgeSpacing.sm),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    state.suggestedIngredients.forEach { name ->
                        FilterChip(
                            selected = false,
                            onClick = { onIntent(PhotoIngredientIntent.ConfirmSuggestedIngredient(name)) },
                            label = { Text(name.replaceFirstChar { it.titlecase() }) },
                            shape = FridgePillShape,
                            leadingIcon = {
                                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.padding(2.dp))
                            },
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = state.manualInput,
            onValueChange = { onIntent(PhotoIngredientIntent.UpdateManualInput(it)) },
            label = { Text("Fehlt etwas? Manuell hinzufügen") },
            trailingIcon = {
                IconButton(onClick = { onIntent(PhotoIngredientIntent.AddManualIngredient) }) {
                    Icon(Icons.Filled.Add, contentDescription = "Hinzufügen")
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth().padding(top = FridgeSpacing.md),
        )

        OutlinedButton(
            onClick = onAddAnotherPhoto,
            shape = FridgePillShape,
            modifier = Modifier.padding(top = FridgeSpacing.md).fillMaxWidth().height(52.dp),
        ) {
            Icon(Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.padding(end = FridgeSpacing.sm))
            Text("Weiteres Foto aufnehmen")
        }

        Button(
            onClick = onConfirm,
            enabled = state.canConfirm,
            shape = FridgePillShape,
            modifier = Modifier.padding(vertical = FridgeSpacing.md).fillMaxWidth().height(52.dp),
        ) {
            Text("${state.confirmedIngredients.size} Zutaten übernehmen")
        }
    }
}
