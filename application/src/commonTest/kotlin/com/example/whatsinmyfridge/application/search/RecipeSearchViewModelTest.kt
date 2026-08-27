package com.example.whatsinmyfridge.application.search

import com.example.whatsinmyfridge.domain.model.Allergy
import com.example.whatsinmyfridge.domain.model.DietType
import com.example.whatsinmyfridge.domain.model.Ingredient
import com.example.whatsinmyfridge.domain.model.Recipe
import com.example.whatsinmyfridge.domain.model.RecipeDetails
import com.example.whatsinmyfridge.domain.model.UserProfile
import com.example.whatsinmyfridge.domain.repository.RecipeRepository
import com.example.whatsinmyfridge.domain.repository.SavedRecipeRepository
import com.example.whatsinmyfridge.domain.repository.UserProfileRepository
import com.example.whatsinmyfridge.domain.usecase.ObserveSavedRecipeIdsUseCase
import com.example.whatsinmyfridge.domain.usecase.RemoveSavedRecipeUseCase
import com.example.whatsinmyfridge.domain.usecase.SaveRecipeUseCase
import com.example.whatsinmyfridge.domain.usecase.SearchRecipesByIngredientsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeSearchViewModelTest {

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(
        repository: RecipeRepository,
        savedRecipeRepository: SavedRecipeRepository = fakeSavedRecipeRepository(),
    ) = RecipeSearchViewModel(
        searchRecipesByIngredients = SearchRecipesByIngredientsUseCase(repository, fakeUserProfileRepository()),
        observeSavedRecipeIds = ObserveSavedRecipeIdsUseCase(savedRecipeRepository),
        saveRecipe = SaveRecipeUseCase(savedRecipeRepository),
        removeSavedRecipe = RemoveSavedRecipeUseCase(savedRecipeRepository),
    )

    @Test
    fun `AddIngredient moves trimmed input into ingredient list`() {
        val vm = viewModel(repository = successRepository(emptyList()))

        vm.onIntent(RecipeSearchIntent.UpdateIngredientInput("  Tomate  "))
        vm.onIntent(RecipeSearchIntent.AddIngredient)

        assertEquals(listOf(Ingredient("Tomate")), vm.state.value.ingredients)
        assertEquals("", vm.state.value.ingredientInput)
    }

    @Test
    fun `Search populates recipes on success`() = runTest {
        val recipe = Recipe(1, "Tomatensuppe", null, emptyList(), emptyList())
        val vm = viewModel(repository = successRepository(listOf(recipe)))
        vm.onIntent(RecipeSearchIntent.UpdateIngredientInput("Tomate"))
        vm.onIntent(RecipeSearchIntent.AddIngredient)

        vm.onIntent(RecipeSearchIntent.Search)

        assertEquals(listOf(recipe), vm.state.value.recipes)
        assertTrue(vm.state.value.errorMessage == null)
    }

    private fun successRepository(recipes: List<Recipe>) = object : RecipeRepository {
        override suspend fun findRecipesByIngredients(
            ingredients: List<Ingredient>,
            dietType: DietType,
            allergies: Set<Allergy>,
            maxMissingIngredients: Int,
            limit: Int,
        ): Result<List<Recipe>> = Result.success(recipes)

        override suspend fun getRecipeDetails(recipeId: Long): Result<RecipeDetails> =
            error("not used in this test")
    }

    private fun fakeSavedRecipeRepository() = object : SavedRecipeRepository {
        override fun observeSavedRecipes(): Flow<List<Recipe>> = MutableStateFlow(emptyList())
        override fun observeSavedRecipeIds(): Flow<Set<Long>> = MutableStateFlow(emptySet())
        override suspend fun saveRecipe(recipe: Recipe) = Unit
        override suspend fun removeRecipe(recipeId: Long) = Unit
    }

    private fun fakeUserProfileRepository() = object : UserProfileRepository {
        override fun observeProfile(): Flow<UserProfile?> = MutableStateFlow(null)
        override suspend fun saveProfile(profile: UserProfile) = Unit
    }
}
