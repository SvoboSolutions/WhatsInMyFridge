package com.example.whatsinmyfridge.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.whatsinmyfridge.core.theme.FridgePillShape
import com.example.whatsinmyfridge.core.theme.FridgeSpacing

/**
 * Vollbild-Kochmodus: ein Zubereitungsschritt nach dem anderen, groß und ohne Ablenkung durch
 * Zutatenliste/Summary. Bewusst kein androidx.compose.ui.window.Dialog (siehe
 * PhotoIngredientDialog) - als normales Overlay in derselben Composition läuft die
 * Fortschrittsanimation über denselben Frame-Takt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookModeOverlay(
    recipeTitle: String,
    steps: List<String>,
    onFinish: () -> Unit,
    onClose: () -> Unit,
) {
    var stepIndex by rememberSaveable { mutableIntStateOf(0) }
    val isLastStep = stepIndex == steps.lastIndex

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Schritt ${stepIndex + 1}/${steps.size}") },
                    navigationIcon = {
                        IconButton(onClick = onClose) {
                            Icon(Icons.Filled.Close, contentDescription = "Kochmodus schließen")
                        }
                    },
                )
            },
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                LinearProgressIndicator(
                    progress = { (stepIndex + 1).toFloat() / steps.size },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    strokeCap = StrokeCap.Round,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )

                Text(
                    recipeTitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = FridgeSpacing.md, vertical = FridgeSpacing.sm),
                )

                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(FridgeSpacing.lg),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        steps[stepIndex],
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Medium,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(FridgeSpacing.md),
                    horizontalArrangement = Arrangement.spacedBy(FridgeSpacing.sm),
                ) {
                    OutlinedButton(
                        onClick = { stepIndex-- },
                        enabled = stepIndex > 0,
                        shape = FridgePillShape,
                        modifier = Modifier.weight(1f).height(52.dp),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        Text("Zurück", modifier = Modifier.padding(start = FridgeSpacing.sm))
                    }

                    Button(
                        onClick = { if (isLastStep) onFinish() else stepIndex++ },
                        shape = FridgePillShape,
                        colors = if (isLastStep) {
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            )
                        } else {
                            ButtonDefaults.buttonColors()
                        },
                        modifier = Modifier.weight(1f).height(52.dp),
                    ) {
                        if (isLastStep) {
                            Icon(Icons.Filled.Check, contentDescription = null)
                            Text("Fertig gekocht", modifier = Modifier.padding(start = FridgeSpacing.sm))
                        } else {
                            Text("Weiter")
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.padding(start = FridgeSpacing.sm))
                        }
                    }
                }
            }
        }
    }
}
