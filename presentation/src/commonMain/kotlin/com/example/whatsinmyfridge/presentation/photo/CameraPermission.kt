package com.example.whatsinmyfridge.presentation.photo

import androidx.compose.runtime.Composable

/**
 * Liefert eine Funktion, die die Kamera-Laufzeitberechtigung anfordert (Android: System-Dialog;
 * iOS: kein separater Schritt nötig, der native Bildauswahl-Dialog fragt selbst danach).
 * [onResult] wird mit dem Ergebnis aufgerufen, bevor die Kamera geöffnet wird.
 */
@Composable
expect fun rememberCameraPermissionLauncher(onResult: (granted: Boolean) -> Unit): () -> Unit
