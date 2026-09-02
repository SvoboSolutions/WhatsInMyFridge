package com.example.whatsinmyfridge.presentation.detail

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.whatsinmyfridge.core.theme.FridgeSpacing

/** Platzhalter im Look von RecipeDetailHeader + Textblöcken, statt eines einzelnen Spinners. */
@Composable
fun RecipeDetailSkeleton(modifier: Modifier = Modifier) {
    val shimmerAlpha by rememberInfiniteTransition(label = "detailShimmer").animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "detailShimmerAlpha",
    )
    val placeholderColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = shimmerAlpha)

    Column(modifier = modifier.fillMaxSize()) {
        SkeletonBlock(
            placeholderColor,
            Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)),
        )

        Row(modifier = Modifier.padding(FridgeSpacing.md)) {
            SkeletonBlock(placeholderColor, Modifier.height(28.dp).fillMaxWidth(0.3f).clip(RoundedCornerShape(50)))
        }

        Column(modifier = Modifier.padding(horizontal = FridgeSpacing.md)) {
            SkeletonBlock(placeholderColor, Modifier.height(20.dp).fillMaxWidth(0.7f).clip(RoundedCornerShape(4.dp)))
            SkeletonBlock(
                placeholderColor,
                Modifier.padding(top = FridgeSpacing.sm).height(14.dp).fillMaxWidth().clip(RoundedCornerShape(4.dp)),
            )
            SkeletonBlock(
                placeholderColor,
                Modifier.padding(top = FridgeSpacing.xs).height(14.dp).fillMaxWidth(0.9f).clip(RoundedCornerShape(4.dp)),
            )
            SkeletonBlock(
                placeholderColor,
                Modifier.padding(top = FridgeSpacing.xs).height(14.dp).fillMaxWidth(0.5f).clip(RoundedCornerShape(4.dp)),
            )
        }
    }
}

@Composable
private fun SkeletonBlock(color: Color, modifier: Modifier) {
    Box(modifier = modifier.background(color))
}
