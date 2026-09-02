package com.example.whatsinmyfridge.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import com.example.whatsinmyfridge.core.theme.FridgeSpacing
import com.example.whatsinmyfridge.domain.model.DietType

@Composable
fun DietSelector(
    selected: DietType,
    onSelect: (DietType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(FridgeSpacing.xs)) {
        DietType.entries.forEach { diet ->
            val isSelected = diet == selected
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FridgeSpacing.smMd),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .selectable(selected = isSelected, onClick = { onSelect(diet) }, role = Role.RadioButton)
                    .background(if (isSelected) MaterialTheme.colorScheme.surfaceContainerHigh else Color.Transparent)
                    .padding(horizontal = FridgeSpacing.sm, vertical = FridgeSpacing.sm),
            ) {
                RadioButton(selected = isSelected, onClick = null)
                Text(
                    diet.label(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                )
            }
        }
    }
}
