package com.example.whatsinmyfridge.presentation.photo

import androidx.compose.runtime.Composable

/**
 * Liefert eine Funktion, die die Kamera-Laufzeitberechtigung anfordert (Android: System-Dialog;
 * iOS: kein separater Schritt nötig, AVFoundation fragt beim ersten Kamerazugriff selbst).
 * [onResult] wird mit dem Ergebnis aufgerufen, bevor die Kamera-Ansicht geöffnet wird.
 */
@Composable
expect fun rememberCameraPermissionLauncher(onResult: (granted: Boolean) -> Unit): () -> Unit
