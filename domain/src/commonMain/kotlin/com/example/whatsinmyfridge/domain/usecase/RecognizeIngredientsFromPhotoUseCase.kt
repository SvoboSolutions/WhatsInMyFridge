package com.example.whatsinmyfridge.domain.usecase

import com.example.whatsinmyfridge.domain.repository.IngredientRecognitionRepository

class RecognizeIngredientsFromPhotoUseCase(
    private val ingredientRecognitionRepository: IngredientRecognitionRepository,
) {
    suspend operator fun invoke(imageBytes: ByteArray): Result<List<String>> =
        ingredientRecognitionRepository.recognizeIngredients(imageBytes)
}
