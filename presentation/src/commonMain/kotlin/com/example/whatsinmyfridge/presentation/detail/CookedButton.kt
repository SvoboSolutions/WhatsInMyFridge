package com.example.whatsinmyfridge.presentation.detail

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.whatsinmyfridge.core.theme.FridgePillShape
import com.example.whatsinmyfridge.core.theme.FridgeSpacing

@Composable
fun CookedButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        shape = FridgePillShape,
        modifier = modifier.padding(FridgeSpacing.md).fillMaxWidth().height(52.dp),
    ) {
        Icon(Icons.Filled.Restaurant, contentDescription = null)
        Text("Habe ich gekocht", modifier = Modifier.padding(start = FridgeSpacing.sm))
    }
}
