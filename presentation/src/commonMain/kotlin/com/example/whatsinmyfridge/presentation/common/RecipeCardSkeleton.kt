package com.example.whatsinmyfridge.presentation.common

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.whatsinmyfridge.core.theme.FridgeSpacing

/**
 * Platzhalter im Look der echten RecipeCard (gleiche Maße/Proportionen) statt eines simplen
 * Spinners - wirkt beim Laden hochwertiger und das Layout "springt" beim Erscheinen der
 * echten Karten nicht.
 */
@Composable
fun RecipeCardSkeleton(modifier: Modifier = Modifier) {
    val shimmerAlpha by rememberShimmerAlpha()
    val placeholderColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = shimmerAlpha)

    ElevatedCard(
        shape = MaterialTheme.shapes.large,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(placeholderColor),
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(FridgeSpacing.smMd),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(18.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(placeholderColor),
                )
                Box(
                    modifier = Modifier
                        .padding(top = FridgeSpacing.xs)
                        .fillMaxWidth(0.4f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(placeholderColor),
                )
            }
            Box(
                modifier = Modifier
                    .padding(start = FridgeSpacing.sm)
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(placeholderColor),
            )
        }
    }
}

@Composable
private fun rememberShimmerAlpha() = rememberInfiniteTransition(label = "shimmer").animateFloat(
    initialValue = 0.35f,
    targetValue = 0.75f,
    animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 700, easing = LinearEasing),
        repeatMode = RepeatMode.Reverse,
    ),
    label = "shimmerAlpha",
)
