package com.example.whatsinmyfridge.presentation.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.example.whatsinmyfridge.application.onboarding.OnboardingIntent
import com.example.whatsinmyfridge.application.onboarding.OnboardingState
import com.example.whatsinmyfridge.application.onboarding.OnboardingStep
import com.example.whatsinmyfridge.core.theme.FridgeMotion
import com.example.whatsinmyfridge.core.theme.FridgePillShape
import com.example.whatsinmyfridge.core.theme.FridgeSpacing

@Composable
fun OnboardingScreen(state: OnboardingState, onIntent: (OnboardingIntent) -> Unit) {
    val stepIndex = OnboardingStep.entries.indexOf(state.step)

    Scaffold { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(FridgeSpacing.lg)) {
            LinearProgressIndicator(
                progress = { (stepIndex + 1) / OnboardingStep.entries.size.toFloat() },
                strokeCap = StrokeCap.Round,
                modifier = Modifier.fillMaxWidth().height(8.dp),
            )

            Box(modifier = Modifier.weight(1f).padding(top = FridgeSpacing.xl), contentAlignment = Alignment.Center) {
                AnimatedContent(
                    targetState = state.step,
                    transitionSpec = {
                        (slideInHorizontally(tween(FridgeMotion.DURATION_MEDIUM)) { it } + fadeIn())
                            .togetherWith(slideOutHorizontally(tween(FridgeMotion.DURATION_MEDIUM)) { -it } + fadeOut())
                    },
                    label = "onboarding-step",
                ) { step ->
                    when (step) {
                        OnboardingStep.WELCOME -> WelcomeStep(
                            displayName = state.displayName,
                            onDisplayNameChange = { onIntent(OnboardingIntent.UpdateDisplayName(it)) },
                        )
                        OnboardingStep.DIET -> DietStep(
                            selected = state.dietType,
                            onSelect = { onIntent(OnboardingIntent.SelectDiet(it)) },
                        )
                        OnboardingStep.ALLERGIES -> AllergyStep(
                            selected = state.allergies,
                            onToggle = { onIntent(OnboardingIntent.ToggleAllergy(it)) },
                        )
                        OnboardingStep.SUMMARY -> SummaryStep(
                            displayName = state.displayName,
                            dietType = state.dietType,
                            allergies = state.allergies,
                        )
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                if (stepIndex > 0) {
                    TextButton(onClick = { onIntent(OnboardingIntent.PreviousStep) }) { Text("Zurück") }
                } else {
                    Box {}
                }

                if (state.step == OnboardingStep.SUMMARY) {
                    Button(
                        onClick = { onIntent(OnboardingIntent.Finish) },
                        enabled = !state.isSaving,
                        shape = FridgePillShape,
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        } else {
                            Text("Los geht's")
                        }
                    }
                } else {
                    Button(
                        onClick = { onIntent(OnboardingIntent.NextStep) },
                        enabled = state.canGoNext,
                        shape = FridgePillShape,
                    ) {
                        Text("Weiter")
                    }
                }
            }
        }
    }
}
