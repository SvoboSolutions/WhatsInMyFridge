package com.example.whatsinmyfridge.presentation.photo

import androidx.compose.runtime.Composable

/**
 * Auf iOS fragt AVFoundation die Kamera-Berechtigung selbst ab, sobald die Kamera-Session
 * startet - kein separater vorgelagerter Schritt nötig.
 */
@Composable
actual fun rememberCameraPermissionLauncher(onResult: (granted: Boolean) -> Unit): () -> Unit =
    { onResult(true) }

actual val isCameraCaptureSupported: Boolean = true
