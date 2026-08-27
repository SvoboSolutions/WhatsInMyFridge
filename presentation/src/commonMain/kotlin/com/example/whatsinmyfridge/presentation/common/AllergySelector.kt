package com.example.whatsinmyfridge.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.whatsinmyfridge.core.theme.FridgePillShape
import com.example.whatsinmyfridge.core.theme.FridgeSpacing
import com.example.whatsinmyfridge.domain.model.Allergy

@Composable
fun AllergySelector(
    selected: Set<Allergy>,
    onToggle: (Allergy) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(FridgeSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(FridgeSpacing.sm),
        modifier = modifier,
    ) {
        Allergy.entries.forEach { allergy ->
            FilterChip(
                selected = allergy in selected,
                onClick = { onToggle(allergy) },
                label = { Text(allergy.label()) },
                shape = FridgePillShape,
            )
        }
    }
}
