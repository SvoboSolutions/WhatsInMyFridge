package com.example.whatsinmyfridge.presentation.common

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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.whatsinmyfridge.domain.model.DietType

@Composable
fun DietSelector(
    selected: DietType,
    onSelect: (DietType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.selectableGroup()) {
        DietType.entries.forEach { diet ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(selected = diet == selected, onClick = { onSelect(diet) }, role = Role.RadioButton)
                    .padding(vertical = 10.dp),
            ) {
                RadioButton(selected = diet == selected, onClick = null)
                Text(diet.label(), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
