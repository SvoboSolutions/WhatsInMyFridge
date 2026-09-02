package com.example.whatsinmyfridge.infrastructure.remote

import com.example.whatsinmyfridge.domain.model.IngredientRecognitionResult
import com.example.whatsinmyfridge.domain.repository.IngredientRecognitionRepository

class IngredientRecognitionRepositoryImpl(
    private val claudeVisionApi: ClaudeVisionApi,
) : IngredientRecognitionRepository {
    override suspend fun recognizeIngredients(imageBytes: ByteArray): Result<IngredientRecognitionResult> =
        claudeVisionApi.recognizeIngredients(imageBytes)
}
