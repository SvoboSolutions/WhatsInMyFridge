package com.example.whatsinmyfridge.presentation.photo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.whatsinmyfridge.application.photo.PhotoIngredientIntent
import com.example.whatsinmyfridge.application.photo.PhotoIngredientState
import com.example.whatsinmyfridge.application.photo.PhotoIngredientStep
import com.example.whatsinmyfridge.application.photo.PhotoIngredientViewModel
import com.example.whatsinmyfridge.core.theme.FridgePillShape
import com.example.whatsinmyfridge.core.theme.FridgeSpacing
import com.preat.peekaboo.image.picker.ResizeOptions
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

private val PHOTO_RESIZE_OPTIONS = ResizeOptions(
    width = 1200,
    height = 1200,
    resizeThresholdBytes = 2 * 1024 * 1024L,
    compressionQuality = 0.7,
)

/**
 * Modal für die KI-Fotoerkennung: Kamera/Galerie wählen -> Foto analysieren ->
 * erkannte Zutaten vor der Übernahme bestätigen/bearbeiten.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoIngredientDialog(
    onDismiss: () -> Unit,
    onConfirm: (List<String>) -> Unit,
    viewModel: PhotoIngredientViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val onIntent = viewModel::onIntent
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            onIntent(PhotoIngredientIntent.DismissError)
        }
    }

    val scope = rememberCoroutineScope()

    val captureFromCamera = rememberCameraCaptureLauncher(onResult = { imageBytes ->
        imageBytes?.let { onIntent(PhotoIngredientIntent.AnalyzePhoto(it)) }
    })

    val requestCameraPermission = rememberCameraPermissionLauncher(onResult = { granted ->
        if (granted) {
            captureFromCamera()
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Kamera-Berechtigung verweigert. Du kannst stattdessen ein Foto aus der Galerie wählen.")
            }
        }
    })

    val galleryPicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        resizeOptions = PHOTO_RESIZE_OPTIONS,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let { onIntent(PhotoIngredientIntent.AnalyzePhoto(it)) }
        },
    )

    // Session-Ende (egal ob per X oder nach Übernahme): Zustand zurücksetzen, damit beim
    // nächsten Öffnen wieder bei der Quellen-Auswahl gestartet wird statt bei alten Daten.
    val dismissAndReset = {
        onIntent(PhotoIngredientIntent.Reset)
        onDismiss()
    }

    // Bewusst kein androidx.compose.ui.window.Dialog: Compose-Animationen (z.B. der Lade-
    // Spinner) frieren in manchen Compose-Versionen in einem separaten Dialog-Fenster ein.
    // Als normales Overlay in derselben Composition läuft alles über denselben Frame-Takt.
    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Zutaten per Foto erkennen") },
                    navigationIcon = {
                        IconButton(onClick = dismissAndReset) {
                            Icon(Icons.Filled.Close, contentDescription = "Schließen")
                        }
                    },
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                when (state.step) {
                    PhotoIngredientStep.SOURCE_CHOICE ->
                        SourceChoiceContent(onPickCamera = requestCameraPermission, onPickGallery = galleryPicker::launch)

                    PhotoIngredientStep.LOADING -> LoadingContent()

                    PhotoIngredientStep.PREVIEW ->
                        PhotoIngredientPreviewContent(
                            state = state,
                            onIntent = onIntent,
                            onAddAnotherPhoto = { onIntent(PhotoIngredientIntent.AddAnotherPhoto) },
                            onConfirm = {
                                onConfirm(state.confirmedIngredients)
                                dismissAndReset()
                            },
                        )
                }
            }
        }
    }
}

@Composable
private fun SourceChoiceContent(onPickCamera: () -> Unit, onPickGallery: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(FridgeSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            Icons.Filled.CameraAlt,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp).padding(bottom = FridgeSpacing.md),
        )
        Text(
            "Fotografiere deinen Kühlschrank oder wähle ein Foto aus - die KI erkennt automatisch, welche Zutaten du hast.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = FridgeSpacing.lg),
        )
        Button(
            onClick = onPickCamera,
            shape = FridgePillShape,
            modifier = Modifier.fillMaxWidth().height(52.dp),
        ) {
            Icon(Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.padding(end = FridgeSpacing.sm))
            Text("Foto aufnehmen")
        }
        OutlinedButton(
            onClick = onPickGallery,
            shape = FridgePillShape,
            modifier = Modifier.padding(top = FridgeSpacing.sm).fillMaxWidth().height(52.dp),
        ) {
            Icon(Icons.Filled.Photo, contentDescription = null, modifier = Modifier.padding(end = FridgeSpacing.sm))
            Text("Aus Galerie wählen")
        }
    }
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(modifier = Modifier.padding(bottom = FridgeSpacing.md))
        Text("Zutaten werden erkannt ...", style = MaterialTheme.typography.bodyMedium)
    }
}
