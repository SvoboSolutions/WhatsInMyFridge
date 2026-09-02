package com.example.whatsinmyfridge.presentation.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
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
import com.example.whatsinmyfridge.domain.model.Recipe

private val CardImageHeight = 150.dp
private val MatchRingSize = 40.dp

@Composable
fun RecipeCard(
    recipe: Recipe,
    isSaved: Boolean,
    onClick: () -> Unit,
    onToggleSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
    ) {
        Box {
            if (recipe.imageUrl != null) {
                AsyncImage(
                    model = recipe.imageUrl,
                    contentDescription = recipe.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(CardImageHeight)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                )
            } else {
                Surface(
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth().height(CardImageHeight),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Filled.Kitchen,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(40.dp),
                        )
                    }
                }
            }

            IconButton(
                onClick = onToggleSave,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
                modifier = Modifier.align(Alignment.TopEnd).padding(FridgeSpacing.sm),
            ) {
                Icon(
                    if (isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                    contentDescription = if (isSaved) "Gespeichert" else "Speichern",
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(FridgeSpacing.sm + FridgeSpacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                recipe.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).padding(end = FridgeSpacing.sm),
            )

            if (recipe.usedIngredients.isNotEmpty() || recipe.missedIngredients.isNotEmpty()) {
                MatchRing(usedCount = recipe.usedIngredients.size, missedCount = recipe.missedIngredientCount)
            }
        }
    }
}

/** Fortschrittsring: Anteil vorhandener Zutaten statt reinem "Fehlt: X"-Text - auf einen Blick erfassbar. */
@Composable
private fun MatchRing(usedCount: Int, missedCount: Int, modifier: Modifier = Modifier) {
    val total = usedCount + missedCount
    val fraction = if (total == 0) 1f else usedCount.toFloat() / total
    val complete = missedCount == 0
    val ringColor = if (complete) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary

    Box(modifier = modifier.size(MatchRingSize), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { fraction },
            modifier = Modifier.size(MatchRingSize),
            color = ringColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeWidth = 4.dp,
            strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
        )
        Text(
            "${(fraction * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = ringColor,
        )
    }
}
