package com.example.whatsinmyfridge.presentation.mealplan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.whatsinmyfridge.core.theme.FridgeSpacing
import com.example.whatsinmyfridge.domain.model.MealPlanEntry
import kotlinx.datetime.LocalDate

/**
 * Ein Tag im Essenplan: entweder mit zugewiesenem Rezept (Bild, Titel, Tausch-/Entfernen-
 * Aktionen) oder leer mit Aufforderung, ein Rezept auszuwählen.
 */
@Composable
fun MealPlanDayCard(
    date: LocalDate,
    entry: MealPlanEntry?,
    onPickRecipe: () -> Unit,
    onRemoveEntry: () -> Unit,
    onOpenDetails: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        shape = MaterialTheme.shapes.large,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(FridgeSpacing.sm + FridgeSpacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.width(FridgeSpacing.xxl)) {
                Text(date.toWeekdayShort(), style = MaterialTheme.typography.labelLarge)
                Text(
                    date.toDayMonth(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (entry != null) {
                Row(
                    modifier = Modifier.weight(1f).clickable(onClick = onOpenDetails),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (entry.recipeImageUrl != null) {
                        AsyncImage(
                            model = entry.recipeImageUrl,
                            contentDescription = entry.recipeTitle,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(52.dp).clip(MaterialTheme.shapes.medium),
                        )
                    } else {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(52.dp),
                        ) {
                            Icon(Icons.Filled.RestaurantMenu, contentDescription = null, modifier = Modifier.padding(FridgeSpacing.sm))
                        }
                    }

                    Text(
                        entry.recipeTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = FridgeSpacing.sm),
                    )
                }

                IconButton(onClick = onPickRecipe) {
                    Icon(Icons.Filled.SwapHoriz, contentDescription = "Rezept ändern")
                }
                IconButton(onClick = onRemoveEntry) {
                    Icon(Icons.Filled.Close, contentDescription = "Entfernen")
                }
            } else {
                Box(modifier = Modifier.weight(1f).padding(horizontal = FridgeSpacing.sm)) {
                    Text(
                        "Kein Rezept geplant",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                OutlinedButton(onClick = onPickRecipe) {
                    Text("Wählen")
                }
            }
        }
    }
}
