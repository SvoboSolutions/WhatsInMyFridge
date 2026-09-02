package com.example.whatsinmyfridge.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.NoFood
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.whatsinmyfridge.application.profile.ProfileIntent
import com.example.whatsinmyfridge.application.profile.ProfileState
import com.example.whatsinmyfridge.core.theme.FridgePillShape
import com.example.whatsinmyfridge.core.theme.FridgeSpacing
import com.example.whatsinmyfridge.presentation.common.AllergySelector
import com.example.whatsinmyfridge.presentation.common.DietSelector
import com.example.whatsinmyfridge.presentation.common.ScreenHeaderRow
import com.example.whatsinmyfridge.presentation.common.ThemeModeSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(state: ProfileState, onIntent: (ProfileIntent) -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Profil") }) },
    ) { padding ->
        val profile = state.profile
        if (state.isLoading || profile == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(FridgeSpacing.lg),
        ) {
            ProfileHeader(displayName = profile.displayName, email = state.email)
            ScoreCard(stats = state.stats)

            ProfileSectionCard(title = "Ernährungsweise", icon = Icons.Filled.Restaurant) {
                DietSelector(
                    selected = profile.dietType,
                    onSelect = { onIntent(ProfileIntent.SelectDiet(it)) },
                )
            }

            ProfileSectionCard(title = "Unverträglichkeiten", icon = Icons.Filled.NoFood) {
                AllergySelector(
                    selected = profile.allergies,
                    onToggle = { onIntent(ProfileIntent.ToggleAllergy(it)) },
                )
            }

            ProfileSectionCard(title = "Darstellung", icon = Icons.Filled.Palette) {
                ThemeModeSelector(
                    selected = profile.themeMode,
                    onSelect = { onIntent(ProfileIntent.SetThemeMode(it)) },
                )
            }

            OutlinedButton(
                onClick = { onIntent(ProfileIntent.SignOut) },
                shape = FridgePillShape,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth().height(48.dp),
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.padding(end = FridgeSpacing.sm))
                Text("Abmelden")
            }
        }
    }
}

@Composable
private fun ProfileSectionCard(title: String, icon: ImageVector, content: @Composable () -> Unit) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(FridgeSpacing.lg - FridgeSpacing.xs)) {
            ScreenHeaderRow(icon = icon, text = title)
            Box(Modifier.padding(top = FridgeSpacing.md)) { content() }
        }
    }
}
