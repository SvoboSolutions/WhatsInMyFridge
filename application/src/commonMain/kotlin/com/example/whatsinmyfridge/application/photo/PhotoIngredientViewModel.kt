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

    fun onIntent(intent: PhotoIngredientIntent) {
        when (intent) {
            is PhotoIngredientIntent.AnalyzePhoto -> analyzePhoto(intent.imageBytes)

            PhotoIngredientIntent.AddAnotherPhoto ->
                _state.update { it.copy(step = PhotoIngredientStep.SOURCE_CHOICE, errorMessage = null) }

            is PhotoIngredientIntent.RemoveIngredient ->
                _state.update { it.copy(confirmedIngredients = it.confirmedIngredients - intent.name) }

            is PhotoIngredientIntent.ConfirmSuggestedIngredient -> confirmSuggested(intent.name)

            is PhotoIngredientIntent.UpdateManualInput ->
                _state.update { it.copy(manualInput = intent.value) }

            PhotoIngredientIntent.AddManualIngredient -> addManualIngredient()

            PhotoIngredientIntent.Reset -> _state.value = PhotoIngredientState()

            PhotoIngredientIntent.DismissError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun confirmSuggested(name: String) {
        _state.update {
            it.copy(
                confirmedIngredients = (it.confirmedIngredients + name).distinct(),
                suggestedIngredients = it.suggestedIngredients - name,
            )
        }
    }

    private fun addManualIngredient() {
        val name = _state.value.manualInput.trim().lowercase()
        if (name.isEmpty()) return
        _state.update {
            it.copy(
                confirmedIngredients = (it.confirmedIngredients + name).distinct(),
                suggestedIngredients = it.suggestedIngredients - name,
                manualInput = "",
            )
        }
    }

    /**
     * Ergebnisse mehrerer Fotos innerhalb einer Session werden zusammengeführt statt sich
     * gegenseitig zu überschreiben - so kann man z.B. erst die Vorratskammer, dann den
     * Kühlschrank fotografieren und alles zusammen übernehmen. Case-insensitiv dedupliziert.
     * Sichere Erkennungen landen direkt in der festen Liste, unsichere als Vorschlag, den
     * man erst antippen muss, damit er übernommen wird.
     */
    private fun analyzePhoto(imageBytes: ByteArray) {
        viewModelScope.launch {
            _state.update { it.copy(step = PhotoIngredientStep.LOADING, errorMessage = null) }

            recognizeIngredientsFromPhoto(imageBytes)
                .onSuccess { result ->
                    _state.update { current ->
                        val confirmed = (current.confirmedIngredients + result.confident)
                            .map { it.trim().lowercase() }
                            .filter { it.isNotBlank() }
                            .distinct()
                        val suggested = (current.suggestedIngredients + result.uncertain)
                            .map { it.trim().lowercase() }
                            .filter { it.isNotBlank() }
                            .distinct()
                            .filterNot { it in confirmed }
                        current.copy(step = PhotoIngredientStep.PREVIEW, confirmedIngredients = confirmed, suggestedIngredients = suggested)
                    }
                }
                .onFailure { error ->
                    Logger.e(TAG, "Zutatenerkennung fehlgeschlagen", error)
                    _state.update { current ->
                        // Bereits erkannte Zutaten aus vorherigen Fotos dieser Session bleiben erhalten.
                        val fallbackStep = if (current.confirmedIngredients.isEmpty() && current.suggestedIngredients.isEmpty()) {
                            PhotoIngredientStep.SOURCE_CHOICE
                        } else {
                            PhotoIngredientStep.PREVIEW
                        }
                        current.copy(step = fallbackStep, errorMessage = error.message ?: "Erkennung fehlgeschlagen")
                    }
                }
        }
    }
}
