package com.example.whatsinmyfridge.presentation.photo

import androidx.compose.runtime.Composable

/**
 * Öffnet die native System-Kamera (Android: Kamera-App via Intent, iOS: UIImagePickerController)
 * und liefert das aufgenommene Foto als JPEG-Bytes zurück, oder null bei Abbruch.
 *
 * Bewusst NICHT die Peekaboo-Bibliothek für die Kameraaufnahme (nur noch für die
 * Galerie-Auswahl) - Peekaboos eigene In-App-Live-Vorschau crasht auf iOS zuverlässig
 * (github.com/onseok/peekaboo Issues #91/#96). Die jahrzehntelang bewährten nativen
 * System-Picker beider Plattformen umgehen dieses Problem komplett.
 */
@Composable
expect fun rememberCameraCaptureLauncher(onResult: (imageBytes: ByteArray?) -> Unit): () -> Unit
