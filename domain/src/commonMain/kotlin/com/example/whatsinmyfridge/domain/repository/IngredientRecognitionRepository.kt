package com.example.whatsinmyfridge.domain.repository

import com.example.whatsinmyfridge.domain.model.IngredientRecognitionResult

/**
 * Erkennt Lebensmittel auf einem Foto (z.B. Kühlschrank-Inhalt) per KI-Bildanalyse.
 */
interface IngredientRecognitionRepository {
    suspend fun recognizeIngredients(imageBytes: ByteArray): Result<IngredientRecognitionResult>
}
