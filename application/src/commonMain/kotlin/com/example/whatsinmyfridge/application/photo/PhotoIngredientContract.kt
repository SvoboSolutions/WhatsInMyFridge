package com.example.whatsinmyfridge.application.photo

enum class PhotoIngredientStep {
    SOURCE_CHOICE,
    LOADING,
    PREVIEW,
}

data class PhotoIngredientState(
    val step: PhotoIngredientStep = PhotoIngredientStep.SOURCE_CHOICE,
    val confirmedIngredients: List<String> = emptyList(),
    val suggestedIngredients: List<String> = emptyList(),
    val manualInput: String = "",
    val errorMessage: String? = null,
) {
    val canConfirm: Boolean get() = confirmedIngredients.isNotEmpty()
}

sealed interface PhotoIngredientIntent {
    data class AnalyzePhoto(val imageBytes: ByteArray) : PhotoIngredientIntent

    /** Zurück zur Kamera/Galerie-Auswahl, ohne die bisher erkannten Zutaten zu verwerfen -
     * für "hat nicht viel gefunden, ich nehme noch ein Foto auf". */
    data object AddAnotherPhoto : PhotoIngredientIntent

    data class RemoveIngredient(val name: String) : PhotoIngredientIntent

    /** Ein unsicherer Vorschlag wird vom Nutzer bestätigt und wandert in die feste Liste. */
    data class ConfirmSuggestedIngredient(val name: String) : PhotoIngredientIntent

    data class UpdateManualInput(val value: String) : PhotoIngredientIntent
    data object AddManualIngredient : PhotoIngredientIntent

    /** Kompletter Reset (neue Session) - beim Schließen des Dialogs aufrufen, damit beim
     * nächsten Öffnen wieder bei der Quellen-Auswahl gestartet wird statt bei der alten
     * (ggf. stale) Vorschau. */
    data object Reset : PhotoIngredientIntent

    data object DismissError : PhotoIngredientIntent
}
