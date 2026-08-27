package com.example.whatsinmyfridge.presentation.mealplan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.whatsinmyfridge.application.mealplan.MealPlanViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * Verdrahtungspunkt: holt das ViewModel per Koin, reicht State/Intent an den reinen Screen weiter.
 */
@Composable
fun MealPlanRoute(viewModel: MealPlanViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    MealPlanScreen(state = state, onIntent = viewModel::onIntent)
}
