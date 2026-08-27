package com.example.whatsinmyfridge.domain.repository

/**
 * Erkennt Lebensmittel auf einem Foto (z.B. Kühlschrank-Inhalt) per KI-Bildanalyse.
 */
interface IngredientRecognitionRepository {
    suspend fun recognizeIngredients(imageBytes: ByteArray): Result<List<String>>
}
