package com.example.whatsinmyfridge.presentation.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.whatsinmyfridge.core.theme.FridgeSpacing
import com.example.whatsinmyfridge.domain.model.DietType
import com.example.whatsinmyfridge.presentation.common.DietSelector

@Composable
fun DietStep(
    selected: DietType,
    onSelect: (DietType) -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            "Wie ernährst du dich?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            "Damit wir Rezepte passend vorschlagen können",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = FridgeSpacing.xs, bottom = FridgeSpacing.lg),
        )
        DietSelector(selected = selected, onSelect = onSelect)
    }
}
