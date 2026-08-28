package com.example.whatsinmyfridge.presentation.photo

import androidx.compose.runtime.Composable

/**
 * Liefert eine Funktion, die die Kamera-Laufzeitberechtigung anfordert (Android: System-Dialog;
 * iOS: kein separater Schritt nötig, AVFoundation fragt beim ersten Kamerazugriff selbst).
 * [onResult] wird mit dem Ergebnis aufgerufen, bevor die Kamera-Ansicht geöffnet wird.
 */
@Composable
expect fun rememberCameraPermissionLauncher(onResult: (granted: Boolean) -> Unit): () -> Unit

/**
 * Peekaboos native In-App-Kamera crasht auf iOS aktuell zuverlässig beim Öffnen
 * (bekannter, offener Bug in der Bibliothek: github.com/onseok/peekaboo Issues #91/#96).
 * Bis das behoben ist, bieten wir dort nur die Galerie-Auswahl an - die nutzt einen
 * anderen, stabilen nativen Pfad (PHPickerViewController) und ist nicht betroffen.
 */
expect val isCameraCaptureSupported: Boolean
