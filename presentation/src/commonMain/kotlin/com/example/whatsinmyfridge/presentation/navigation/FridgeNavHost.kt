package com.example.whatsinmyfridge.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.whatsinmyfridge.presentation.detail.RecipeDetailRoute
import com.example.whatsinmyfridge.presentation.mealplan.MealPlanRoute
import com.example.whatsinmyfridge.presentation.pantry.PantryRoute
import com.example.whatsinmyfridge.presentation.profile.ProfileRoute
import com.example.whatsinmyfridge.presentation.saved.SavedRecipesRoute
import com.example.whatsinmyfridge.presentation.search.RecipeSearchRoute
import com.example.whatsinmyfridge.presentation.settings.SettingsRoute

@Composable
fun FridgeNavHost(navController: NavHostController = rememberNavController()) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val isOnSearch = currentRoute == FridgeDestination.RecipeSearch::class.qualifiedName
    val isOnPantry = currentRoute == FridgeDestination.Pantry::class.qualifiedName
    val isOnMealPlan = currentRoute == FridgeDestination.MealPlan::class.qualifiedName
    val isOnSaved = currentRoute == FridgeDestination.SavedRecipes::class.qualifiedName
    val isOnProfile = currentRoute == FridgeDestination.Profile::class.qualifiedName
    val showBottomBar = currentRoute in bottomNavRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = isOnSearch,
                        onClick = { navController.navigateToTopLevel(FridgeDestination.RecipeSearch) },
                        icon = { Icon(Icons.Filled.Search, contentDescription = null) },
                        label = { Text("Suche") },
                    )
                    NavigationBarItem(
                        selected = isOnPantry,
                        onClick = { navController.navigateToTopLevel(FridgeDestination.Pantry) },
                        icon = { Icon(Icons.Filled.Kitchen, contentDescription = null) },
                        label = { Text("Vorräte") },
                    )
                    NavigationBarItem(
                        selected = isOnMealPlan,
                        onClick = { navController.navigateToTopLevel(FridgeDestination.MealPlan) },
                        icon = { Icon(Icons.Filled.CalendarMonth, contentDescription = null) },
                        label = { Text("Plan") },
                    )
                    NavigationBarItem(
                        selected = isOnSaved,
                        onClick = { navController.navigateToTopLevel(FridgeDestination.SavedRecipes) },
                        icon = {
                            Icon(
                                if (isOnSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                contentDescription = null,
                            )
                        },
                        label = { Text("Gespeichert") },
                    )
                    NavigationBarItem(
                        selected = isOnProfile,
                        onClick = { navController.navigateToTopLevel(FridgeDestination.Profile) },
                        icon = { Icon(Icons.Filled.Person, contentDescription = null) },
                        label = { Text("Profil") },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = FridgeDestination.RecipeSearch,
            modifier = Modifier.padding(padding),
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition,
        ) {
            composable<FridgeDestination.RecipeSearch> {
                RecipeSearchRoute(
                    onRecipeClick = { recipe ->
                        navController.navigate(
                            FridgeDestination.RecipeDetail(
                                recipeId = recipe.id,
                                usedIngredientNames = recipe.usedIngredients.map { it.name },
                                missedIngredientNames = recipe.missedIngredients.map { it.name },
                            ),
                        )
                    },
                )
            }
            composable<FridgeDestination.Pantry> {
                PantryRoute()
            }
            composable<FridgeDestination.MealPlan> {
                MealPlanRoute()
            }
            composable<FridgeDestination.SavedRecipes> {
                SavedRecipesRoute(
                    onRecipeClick = { recipe ->
                        navController.navigate(
                            FridgeDestination.RecipeDetail(
                                recipeId = recipe.id,
                                usedIngredientNames = recipe.usedIngredients.map { it.name },
                                missedIngredientNames = recipe.missedIngredients.map { it.name },
                            ),
                        )
                    },
                )
            }
            composable<FridgeDestination.Profile> {
                ProfileRoute(onSettingsClick = { navController.navigate(FridgeDestination.Settings) })
            }
            composable<FridgeDestination.Settings> {
                SettingsRoute(onBack = { navController.popBackStack() })
            }
            composable<FridgeDestination.RecipeDetail> { backStackEntry ->
                val route: FridgeDestination.RecipeDetail = backStackEntry.toRoute()
                RecipeDetailRoute(
                    recipeId = route.recipeId,
                    usedIngredientNames = route.usedIngredientNames,
                    missedIngredientNames = route.missedIngredientNames,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}

private fun NavHostController.navigateToTopLevel(route: FridgeDestination) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
