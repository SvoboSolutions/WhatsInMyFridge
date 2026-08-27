package com.example.whatsinmyfridge.presentation.pantry

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.whatsinmyfridge.application.pantry.PantryIntent
import com.example.whatsinmyfridge.application.pantry.PantryViewModel
import com.example.whatsinmyfridge.presentation.photo.PhotoIngredientDialog
import org.koin.compose.viewmodel.koinViewModel

/**
 * Verdrahtungspunkt: holt das ViewModel per Koin, reicht State/Intent an den reinen Screen weiter.
 */
@Composable
fun PantryRoute(viewModel: PantryViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    var showPhotoRecognition by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        PantryScreen(
            state = state,
            onIntent = viewModel::onIntent,
            onOpenPhotoRecognition = { showPhotoRecognition = true },
        )

        if (showPhotoRecognition) {
            PhotoIngredientDialog(
                onDismiss = { showPhotoRecognition = false },
                onConfirm = { names -> viewModel.onIntent(PantryIntent.AddIngredients(names)) },
            )
        }
    }
}
