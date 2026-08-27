package com.example.whatsinmyfridge.application.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsinmyfridge.core.logging.Logger
import com.example.whatsinmyfridge.domain.model.Ingredient
import com.example.whatsinmyfridge.domain.model.Recipe
import com.example.whatsinmyfridge.domain.model.RecipeDetails
import com.example.whatsinmyfridge.domain.usecase.GetRecipeDetailsUseCase
import com.example.whatsinmyfridge.domain.usecase.MarkRecipeAsCookedUseCase
import com.example.whatsinmyfridge.domain.usecase.ObserveSavedRecipeIdsUseCase
import com.example.whatsinmyfridge.domain.usecase.RemoveSavedRecipeUseCase
import com.example.whatsinmyfridge.domain.usecase.SaveRecipeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "RecipeDetailViewModel"

data class RecipeDetailState(
    val isLoading: Boolean = true,
    val details: RecipeDetails? = null,
    val haveIngredients: List<String> = emptyList(),
    val needIngredients: List<String> = emptyList(),
    val isSaved: Boolean = false,
    val justCookedPoints: Int? = null,
    val errorMessage: String? = null,
)

sealed interface RecipeDetailIntent {
    data object ToggleSave : RecipeDetailIntent
    data object MarkAsCooked : RecipeDetailIntent
    data object DismissCookedFeedback : RecipeDetailIntent
}

class RecipeDetailViewModel(
    private val recipeId: Long,
    private val usedIngredientNames: List<String>,
    private val missedIngredientNames: List<String>,
    private val getRecipeDetails: GetRecipeDetailsUseCase,
    observeSavedRecipeIds: ObserveSavedRecipeIdsUseCase,
    private val saveRecipe: SaveRecipeUseCase,
    private val removeSavedRecipe: RemoveSavedRecipeUseCase,
    private val markRecipeAsCooked: MarkRecipeAsCookedUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(RecipeDetailState())
    val state: StateFlow<RecipeDetailState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeSavedRecipeIds().collect { ids ->
                _state.update { it.copy(isSaved = recipeId in ids) }
            }
        }
        viewModelScope.launch {
            getRecipeDetails(recipeId)
                .onSuccess { details ->
                    val (need, have) = classifyIngredients(details.ingredients, missedIngredientNames)
                    _state.update {
                        it.copy(isLoading = false, details = details, haveIngredients = have, needIngredients = need)
                    }
                }
                .onFailure { error ->
                    Logger.e(TAG, "getRecipeDetails failed", error)
                    _state.update {
                        it.copy(isLoading = false, errorMessage = error.message ?: "Unbekannter Fehler")
                    }
                }
        }
    }

    fun onIntent(intent: RecipeDetailIntent) {
        when (intent) {
            RecipeDetailIntent.ToggleSave -> toggleSave()
            RecipeDetailIntent.MarkAsCooked -> markAsCooked()
            RecipeDetailIntent.DismissCookedFeedback -> _state.update { it.copy(justCookedPoints = null) }
        }
    }

    private fun toggleSave() {
        val details = _state.value.details ?: return
        viewModelScope.launch {
            if (_state.value.isSaved) {
                removeSavedRecipe(recipeId)
            } else {
                saveRecipe(
                    Recipe(
                        id = details.id,
                        title = details.title,
                        imageUrl = details.imageUrl,
                        usedIngredients = usedIngredientNames.map { Ingredient(it) },
                        missedIngredients = missedIngredientNames.map { Ingredient(it) },
                    ),
                )
            }
        }
    }

    private fun markAsCooked() {
        val details = _state.value.details ?: return
        viewModelScope.launch {
            markRecipeAsCooked(
                recipeId = recipeId,
                recipeTitle = details.title,
                usedIngredientCount = usedIngredientNames.size,
                missedIngredientCount = missedIngredientNames.size,
            )
            _state.update { it.copy(justCookedPoints = usedIngredientNames.size) }
        }
    }
}

/**
 * Ordnet die vollständige Zutatenliste (mit Mengenangaben) den beim Suchen ermittelten
 * fehlenden Zutaten zu. Alles ohne Treffer gilt als "schon vorhanden" (konservativer
 * Default wäre "brauchst du noch" - aber Gewürze/Basiszutaten tauchen selten in der
 * Missing-Liste auf und würden sonst fälschlich als fehlend markiert).
 */
private fun classifyIngredients(
    fullIngredientLines: List<String>,
    missedIngredientNames: List<String>,
): Pair<List<String>, List<String>> {
    val needKeywords = missedIngredientNames.map { it.lowercase() }.filter { it.isNotBlank() }
    if (needKeywords.isEmpty()) return emptyList<String>() to fullIngredientLines

    return fullIngredientLines.partition { line ->
        val lowerLine = line.lowercase()
        needKeywords.any { keyword -> lowerLine.contains(keyword) }
    }
}
