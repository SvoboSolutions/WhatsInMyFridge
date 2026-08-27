package com.example.whatsinmyfridge.application.photo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsinmyfridge.core.logging.Logger
import com.example.whatsinmyfridge.domain.usecase.RecognizeIngredientsFromPhotoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "PhotoIngredientViewModel"

class PhotoIngredientViewModel(
    private val recognizeIngredientsFromPhoto: RecognizeIngredientsFromPhotoUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(PhotoIngredientState())
    val state: StateFlow<PhotoIngredientState> = _state.asStateFlow()

    private var lastImageBytes: ByteArray? = null

    fun onIntent(intent: PhotoIngredientIntent) {
        when (intent) {
            PhotoIngredientIntent.ShowCamera ->
                _state.update { it.copy(step = PhotoIngredientStep.CAMERA, errorMessage = null) }

            PhotoIngredientIntent.ShowSourceChoice ->
                _state.update { it.copy(step = PhotoIngredientStep.SOURCE_CHOICE, errorMessage = null) }

            is PhotoIngredientIntent.AnalyzePhoto -> analyzePhoto(intent.imageBytes)

            is PhotoIngredientIntent.RemoveIngredient ->
                _state.update { it.copy(recognizedIngredients = it.recognizedIngredients - intent.name) }

            is PhotoIngredientIntent.UpdateManualInput ->
                _state.update { it.copy(manualInput = intent.value) }

            PhotoIngredientIntent.AddManualIngredient -> addManualIngredient()

            PhotoIngredientIntent.Retry -> lastImageBytes?.let { analyzePhoto(it) }

            PhotoIngredientIntent.DismissError ->
                _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun addManualIngredient() {
        val name = _state.value.manualInput.trim().lowercase()
        if (name.isEmpty()) return
        _state.update {
            it.copy(
                recognizedIngredients = (it.recognizedIngredients + name).distinct(),
                manualInput = "",
            )
        }
    }

    private fun analyzePhoto(imageBytes: ByteArray) {
        lastImageBytes = imageBytes
        viewModelScope.launch {
            _state.update { it.copy(step = PhotoIngredientStep.LOADING, errorMessage = null) }

            recognizeIngredientsFromPhoto(imageBytes)
                .onSuccess { names ->
                    _state.update {
                        it.copy(step = PhotoIngredientStep.PREVIEW, recognizedIngredients = names)
                    }
                }
                .onFailure { error ->
                    Logger.e(TAG, "Zutatenerkennung fehlgeschlagen", error)
                    _state.update {
                        it.copy(
                            step = PhotoIngredientStep.SOURCE_CHOICE,
                            errorMessage = error.message ?: "Erkennung fehlgeschlagen",
                        )
                    }
                }
        }
    }
}
