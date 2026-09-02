package com.example.whatsinmyfridge.domain.usecase

import com.example.whatsinmyfridge.domain.model.Allergy
import com.example.whatsinmyfridge.domain.model.DietType
import com.example.whatsinmyfridge.domain.model.Ingredient
import com.example.whatsinmyfridge.domain.model.Recipe
import com.example.whatsinmyfridge.domain.model.RecipeDetails
import com.example.whatsinmyfridge.domain.model.UserProfile
import com.example.whatsinmyfridge.domain.repository.AiRecipeRepository
import com.example.whatsinmyfridge.domain.repository.RecipeRepository
import com.example.whatsinmyfridge.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SearchRecipesByIngredientsUseCaseTest {

    private val fakeAiRecipeRepository = object : AiRecipeRepository {
        override suspend fun suggestRecipe(
            ingredients: List<Ingredient>,
            dietType: DietType,
            allergies: Set<Allergy>,
            allowExtraIngredients: Boolean,
            excludeTitles: List<String>,
        ): Result<Recipe> = error("not used in this test")

        override suspend fun getCachedDetails(recipeId: Long): RecipeDetails? = error("not used in this test")
    }

    private val fakeRepository = object : RecipeRepository {
        override suspend fun findRecipesByIngredients(
            ingredients: List<Ingredient>,
            dietType: DietType,
            allergies: Set<Allergy>,
            maxMissingIngredients: Int,
            limit: Int,
        ): Result<List<Recipe>> = Result.success(
            listOf(Recipe(id = 1, title = "Tomatensuppe", imageUrl = null, usedIngredients = ingredients, missedIngredients = emptyList()))
        )

        override suspend fun getRecipeDetails(recipeId: Long): Result<RecipeDetails> =
            error("not used in this test")
    }

    private fun fakeProfileRepository(profile: UserProfile? = null) = object : UserProfileRepository {
        override fun observeProfile(): Flow<UserProfile?> = MutableStateFlow(profile)
        override suspend fun saveProfile(profile: UserProfile) = Unit
    }

    private val useCase = SearchRecipesByIngredientsUseCase(fakeRepository, fakeAiRecipeRepository, fakeProfileRepository())

    @Test
    fun `returns empty list without calling repository when no ingredients given`() = runTest {
        val emptyRepository = object : RecipeRepository {
            override suspend fun findRecipesByIngredients(
                ingredients: List<Ingredient>,
                dietType: DietType,
                allergies: Set<Allergy>,
                maxMissingIngredients: Int,
                limit: Int,
            ): Result<List<Recipe>> = error("should not be called")

            override suspend fun getRecipeDetails(recipeId: Long): Result<RecipeDetails> =
                error("should not be called")
        }

        val result = SearchRecipesByIngredientsUseCase(emptyRepository, fakeAiRecipeRepository, fakeProfileRepository())(emptyList())

        assertTrue(result.isSuccess)
        assertEquals(emptyList(), result.getOrNull())
    }

    @Test
    fun `delegates to repository when ingredients given`() = runTest {
        val result = useCase(listOf(Ingredient("Tomate")))

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
    }

    @Test
    fun `passes diet and allergies from profile to repository`() = runTest {
        var capturedDiet: DietType? = null
        var capturedAllergies: Set<Allergy>? = null
        val trackingRepository = object : RecipeRepository {
            override suspend fun findRecipesByIngredients(
                ingredients: List<Ingredient>,
                dietType: DietType,
                allergies: Set<Allergy>,
                maxMissingIngredients: Int,
                limit: Int,
            ): Result<List<Recipe>> {
                capturedDiet = dietType
                capturedAllergies = allergies
                return Result.success(emptyList())
            }

            override suspend fun getRecipeDetails(recipeId: Long): Result<RecipeDetails> =
                error("not used in this test")
        }
        val profile = UserProfile(
            uid = "u1",
            displayName = "Max",
            dietType = DietType.VEGAN,
            allergies = setOf(Allergy.GLUTEN),
        )

        SearchRecipesByIngredientsUseCase(trackingRepository, fakeAiRecipeRepository, fakeProfileRepository(profile))(listOf(Ingredient("Tomate")))

        assertEquals(DietType.VEGAN, capturedDiet)
        assertEquals(setOf(Allergy.GLUTEN), capturedAllergies)
    }
}
