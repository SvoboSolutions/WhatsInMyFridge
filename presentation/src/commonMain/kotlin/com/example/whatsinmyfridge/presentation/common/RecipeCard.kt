package com.example.whatsinmyfridge.presentation.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.whatsinmyfridge.core.theme.FridgePillShape
import com.example.whatsinmyfridge.core.theme.FridgeSpacing
import com.example.whatsinmyfridge.domain.model.Recipe

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
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(FridgeSpacing.sm + FridgeSpacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (recipe.imageUrl != null) {
                AsyncImage(
                    model = recipe.imageUrl,
                    contentDescription = recipe.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(68.dp).clip(MaterialTheme.shapes.medium),
                )
            } else {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(68.dp),
                ) {
                    Icon(
                        Icons.Filled.Kitchen,
                        contentDescription = null,
                        modifier = Modifier.padding(FridgeSpacing.md),
                    )
                }
            }

            Column(modifier = Modifier.weight(1f).padding(horizontal = FridgeSpacing.sm + FridgeSpacing.xs)) {
                Text(recipe.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                IngredientStatusBadge(missedCount = recipe.missedIngredientCount)
            }

            IconButton(onClick = onToggleSave) {
                Icon(
                    if (isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                    contentDescription = if (isSaved) "Gespeichert" else "Speichern",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun IngredientStatusBadge(missedCount: Int, modifier: Modifier = Modifier) {
    val complete = missedCount == 0
    Surface(
        shape = FridgePillShape,
        color = if (complete) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.padding(top = FridgeSpacing.xs),
    ) {
        Text(
            if (complete) "Alle Zutaten vorhanden" else "Fehlt: $missedCount Zutat(en)",
            style = MaterialTheme.typography.labelSmall,
            color = if (complete) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = FridgeSpacing.sm, vertical = 3.dp),
        )
    }
}
