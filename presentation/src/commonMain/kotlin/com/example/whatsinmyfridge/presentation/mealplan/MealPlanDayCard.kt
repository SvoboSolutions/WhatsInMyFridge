package com.example.whatsinmyfridge.presentation.mealplan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.whatsinmyfridge.core.theme.FridgeSpacing
import com.example.whatsinmyfridge.domain.model.MealPlanEntry
import kotlinx.datetime.LocalDate

private val DayBadgeSize = 56.dp
private val RowHeight = 84.dp
private val ImageSize = 64.dp

/**
 * Ein Tag im Essenplan: entweder mit zugewiesenem Rezept (Bild, Titel, Tausch-/Entfernen-
 * Aktionen) oder als einladende "Rezept wählen"-Karte mit dezenter Outline-Kontur. Der
 * heutige Tag bekommt eine farbige Tages-Badge statt neutralem Grau, damit er sofort auffällt.
 */
@Composable
fun MealPlanDayCard(
    date: LocalDate,
    entry: MealPlanEntry?,
    isToday: Boolean,
    onPickRecipe: () -> Unit,
    onRemoveEntry: () -> Unit,
    onOpenDetails: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (entry != null) {
        ElevatedCard(
            shape = MaterialTheme.shapes.large,
            modifier = modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(RowHeight).padding(FridgeSpacing.smMd),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DayBadge(date = date, isToday = isToday, filled = true)

                Row(
                    modifier = Modifier.weight(1f).fillMaxHeight().padding(start = FridgeSpacing.sm).clickable(onClick = onOpenDetails),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (entry.recipeImageUrl != null) {
                        AsyncImage(
                            model = entry.recipeImageUrl,
                            contentDescription = entry.recipeTitle,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(ImageSize).clip(RoundedCornerShape(16.dp)),
                        )
                    } else {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(ImageSize),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.RestaurantMenu, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Text(
                        entry.recipeTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = FridgeSpacing.sm),
                    )
                }

                IconButton(
                    onClick = onPickRecipe,
                    colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                ) {
                    Icon(Icons.Filled.SwapHoriz, contentDescription = "Rezept ändern")
                }
                IconButton(onClick = onRemoveEntry) {
                    Icon(Icons.Filled.Close, contentDescription = "Entfernen", tint = MaterialTheme.colorScheme.outline)
                }
            }
        }
    } else {
        OutlinedCard(
            onClick = onPickRecipe,
            shape = MaterialTheme.shapes.large,
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(RowHeight).padding(horizontal = FridgeSpacing.smMd),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DayBadge(date = date, isToday = isToday, filled = false)

                Text(
                    "Kein Rezept geplant",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f).padding(horizontal = FridgeSpacing.sm),
                )

                Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.primaryContainer) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = FridgeSpacing.sm, vertical = FridgeSpacing.xs),
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            "Wählen",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(start = FridgeSpacing.xs),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayBadge(date: LocalDate, isToday: Boolean, filled: Boolean, modifier: Modifier = Modifier) {
    val containerColor = when {
        isToday -> MaterialTheme.colorScheme.primary
        filled -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when {
        isToday -> MaterialTheme.colorScheme.onPrimary
        filled -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        modifier = modifier.size(DayBadgeSize),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    date.toWeekdayShort(),
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    date.toDayMonth().substringBefore("."),
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
