package com.example.whatsinmyfridge.domain.usecase

import com.example.whatsinmyfridge.domain.model.IngredientRecognitionResult
import com.example.whatsinmyfridge.domain.repository.IngredientRecognitionRepository

class RecognizeIngredientsFromPhotoUseCase(
    private val ingredientRecognitionRepository: IngredientRecognitionRepository,
) {
    suspend operator fun invoke(imageBytes: ByteArray): Result<IngredientRecognitionResult> =
        ingredientRecognitionRepository.recognizeIngredients(imageBytes)
}
