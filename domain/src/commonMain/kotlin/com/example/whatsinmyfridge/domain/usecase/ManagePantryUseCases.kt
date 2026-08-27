package com.example.whatsinmyfridge.domain.usecase

import com.example.whatsinmyfridge.domain.model.Ingredient
import com.example.whatsinmyfridge.domain.repository.PantryRepository
import kotlinx.coroutines.flow.Flow

class ObservePantryUseCase(private val pantryRepository: PantryRepository) {
    operator fun invoke(): Flow<List<Ingredient>> = pantryRepository.observeSavedIngredients()
}

class AddPantryIngredientUseCase(private val pantryRepository: PantryRepository) {
    suspend operator fun invoke(ingredient: Ingredient) = pantryRepository.addIngredient(ingredient)
}

class RemovePantryIngredientUseCase(private val pantryRepository: PantryRepository) {
    suspend operator fun invoke(ingredient: Ingredient) = pantryRepository.removeIngredient(ingredient)
}
