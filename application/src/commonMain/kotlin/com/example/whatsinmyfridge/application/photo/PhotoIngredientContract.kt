package com.example.whatsinmyfridge.application.photo

enum class PhotoIngredientStep {
    SOURCE_CHOICE,
    CAMERA,
    LOADING,
    PREVIEW,
}

data class PhotoIngredientState(
    val step: PhotoIngredientStep = PhotoIngredientStep.SOURCE_CHOICE,
    val recognizedIngredients: List<String> = emptyList(),
    val manualInput: String = "",
    val errorMessage: String? = null,
) {
    val canConfirm: Boolean get() = recognizedIngredients.isNotEmpty()
}

sealed interface PhotoIngredientIntent {
    data object ShowCamera : PhotoIngredientIntent
    data object ShowSourceChoice : PhotoIngredientIntent
    data class AnalyzePhoto(val imageBytes: ByteArray) : PhotoIngredientIntent
    data class RemoveIngredient(val name: String) : PhotoIngredientIntent
    data class UpdateManualInput(val value: String) : PhotoIngredientIntent
    data object AddManualIngredient : PhotoIngredientIntent
    data object Retry : PhotoIngredientIntent
    data object DismissError : PhotoIngredientIntent
}
