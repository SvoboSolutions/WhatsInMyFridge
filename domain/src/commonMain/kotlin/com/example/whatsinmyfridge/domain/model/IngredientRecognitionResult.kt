package com.example.whatsinmyfridge.domain.model

/**
 * Ergebnis der Foto-Erkennung, aufgeteilt nach Sicherheit: "confident" wird direkt
 * übernommen, "uncertain" wird nur als Vorschlag angezeigt und muss aktiv bestätigt werden.
 */
data class IngredientRecognitionResult(
    val confident: List<String>,
    val uncertain: List<String>,
)
