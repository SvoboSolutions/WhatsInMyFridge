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
import com.example.whatsinmyfridge.domain.model.Allergy
import com.example.whatsinmyfridge.presentation.common.AllergySelector

@Composable
fun AllergyStep(
    selected: Set<Allergy>,
    onToggle: (Allergy) -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            "Unverträglichkeiten?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            "Optional - kannst du jederzeit in den Einstellungen ändern",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = FridgeSpacing.xs, bottom = FridgeSpacing.lg),
        )
        AllergySelector(selected = selected, onToggle = onToggle)
    }
}
