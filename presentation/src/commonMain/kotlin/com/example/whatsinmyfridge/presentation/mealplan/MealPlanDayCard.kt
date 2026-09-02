package com.example.whatsinmyfridge.presentation.mealplan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.whatsinmyfridge.core.theme.FridgeSpacing
import com.example.whatsinmyfridge.domain.model.MealPlanEntry
import kotlinx.datetime.LocalDate

private val DayBadgeSize = 56.dp
private val RowHeight = 88.dp
private val ImageSize = 68.dp
private val ActionButtonSize = 40.dp

/**
 * Ein Tag im Essenplan: entweder mit zugewiesenem Rezept (Bild, Titel, Tausch-/Entfernen-
 * Aktionen in schwebenden Tonal-Kreisen) oder als einladende "Rezept wählen"-Karte mit
 * dezenter Outline-Kontur. Der heutige Tag bekommt eine farbige, leicht schwebende
 * Tages-Badge statt neutralem Grau, damit er sofort auffällt.
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
    val shape = MaterialTheme.shapes.large

    if (entry != null) {
        Card(
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 5.dp,
                    shape = shape,
                    ambientColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                    spotColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                ),
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
                            modifier = Modifier
                                .size(ImageSize)
                                .shadow(elevation = 3.dp, shape = MaterialTheme.shapes.medium)
                                .clip(MaterialTheme.shapes.medium),
                        )
                    } else {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
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

                DayCardActionButton(
                    icon = Icons.Filled.SwapHoriz,
                    contentDescription = "Rezept ändern",
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    onClick = onPickRecipe,
                )
                DayCardActionButton(
                    icon = Icons.Filled.Close,
                    contentDescription = "Entfernen",
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = onRemoveEntry,
                    modifier = Modifier.padding(start = FridgeSpacing.xs),
                )
            }
        }
    } else {
        OutlinedCard(
            onClick = onPickRecipe,
            shape = shape,
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

                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.shadow(
                        elevation = 3.dp,
                        shape = MaterialTheme.shapes.medium,
                        ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    ),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = FridgeSpacing.smMd, vertical = FridgeSpacing.sm),
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
                            fontWeight = FontWeight.SemiBold,
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
private fun DayCardActionButton(
    icon: ImageVector,
    contentDescription: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(ActionButtonSize)
            .shadow(elevation = 2.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(containerColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = contentDescription, tint = contentColor, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun DayBadge(date: LocalDate, isToday: Boolean, filled: Boolean, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(16.dp)
    val containerColor = when {
        isToday -> MaterialTheme.colorScheme.primary
        filled -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceContainerHighest
    }
    val contentColor = when {
        isToday -> MaterialTheme.colorScheme.onPrimary
        filled -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        shape = shape,
        color = containerColor,
        modifier = modifier.size(DayBadgeSize).let {
            if (isToday) {
                it.shadow(
                    elevation = 6.dp,
                    shape = shape,
                    ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                )
            } else {
                it
            }
        },
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
