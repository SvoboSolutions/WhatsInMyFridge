package com.example.whatsinmyfridge.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import com.example.whatsinmyfridge.core.theme.FridgeMotion

internal val enterTransition: AnimatedContentTransitionScope<*>.() -> androidx.compose.animation.EnterTransition = {
    slideInHorizontally(tween(FridgeMotion.DURATION_MEDIUM)) { it / 4 } + fadeIn(tween(FridgeMotion.DURATION_MEDIUM))
}

internal val exitTransition: AnimatedContentTransitionScope<*>.() -> androidx.compose.animation.ExitTransition = {
    fadeOut(tween(FridgeMotion.DURATION_SHORT))
}

internal val popEnterTransition: AnimatedContentTransitionScope<*>.() -> androidx.compose.animation.EnterTransition = {
    fadeIn(tween(FridgeMotion.DURATION_MEDIUM))
}

internal val popExitTransition: AnimatedContentTransitionScope<*>.() -> androidx.compose.animation.ExitTransition = {
    slideOutHorizontally(tween(FridgeMotion.DURATION_MEDIUM)) { it / 4 } + fadeOut(tween(FridgeMotion.DURATION_SHORT))
}
