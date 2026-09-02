package com.example.whatsinmyfridge.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.whatsinmyfridge.core.theme.FridgePillShape
import com.example.whatsinmyfridge.core.theme.FridgeSpacing
import com.example.whatsinmyfridge.domain.model.RecipeDetails

@Composable
fun RecipeDetailHeader(details: RecipeDetails) {
    if (details.imageUrl != null) {
        AsyncImage(
            model = details.imageUrl,
            contentDescription = details.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(220.dp)
                .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)),
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(FridgeSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(FridgeSpacing.sm),
    ) {
        details.readyInMinutes?.let { InfoChip(Icons.Filled.Schedule, "$it Min.") }
        details.servings?.let { InfoChip(Icons.Filled.Groups, "Für $it Portionen") }
    }
}

@Composable
private fun InfoChip(icon: ImageVector, text: String) {
    Surface(shape = FridgePillShape, color = MaterialTheme.colorScheme.secondaryContainer) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = FridgeSpacing.smMd, vertical = FridgeSpacing.xs),
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            Text(
                text,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(start = FridgeSpacing.xs),
            )
        }
    }
}
