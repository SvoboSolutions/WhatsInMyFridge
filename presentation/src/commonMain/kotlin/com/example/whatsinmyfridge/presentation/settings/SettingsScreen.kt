package com.example.whatsinmyfridge.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.whatsinmyfridge.application.settings.SettingsIntent
import com.example.whatsinmyfridge.application.settings.SettingsState
import com.example.whatsinmyfridge.core.theme.FridgePillShape
import com.example.whatsinmyfridge.core.theme.FridgeSpacing
import com.example.whatsinmyfridge.presentation.common.AllergySelector
import com.example.whatsinmyfridge.presentation.common.DietSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(state: SettingsState, onIntent: (SettingsIntent) -> Unit, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Einstellungen") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                },
            )
        },
    ) { padding ->
        val profile = state.profile
        if (state.isLoading || profile == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(FridgeSpacing.lg - FridgeSpacing.xs),
            verticalArrangement = Arrangement.spacedBy(FridgeSpacing.lg),
        ) {
            SettingsSection(title = "Ernährungsweise") {
                DietSelector(
                    selected = profile.dietType,
                    onSelect = { onIntent(SettingsIntent.SelectDiet(it)) },
                )
            }

            HorizontalDivider()

            SettingsSection(title = "Unverträglichkeiten") {
                AllergySelector(
                    selected = profile.allergies,
                    onToggle = { onIntent(SettingsIntent.ToggleAllergy(it)) },
                )
            }

            HorizontalDivider()

            OutlinedButton(
                onClick = { onIntent(SettingsIntent.SignOut) },
                shape = FridgePillShape,
                modifier = Modifier.fillMaxWidth().height(48.dp),
            ) {
                Text("Abmelden")
            }
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Box(Modifier.padding(top = FridgeSpacing.sm)) { content() }
    }
}
