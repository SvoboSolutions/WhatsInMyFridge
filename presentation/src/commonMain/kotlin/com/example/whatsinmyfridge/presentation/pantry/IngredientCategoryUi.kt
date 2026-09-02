package com.example.whatsinmyfridge.presentation.pantry

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.whatsinmyfridge.domain.model.IngredientCategory

/**
 * Icon + Akzentfarbe je Kategorie für die Vorratskammer-Übersicht. Feste (nicht Theme-
 * abhängige) Farben, damit die Kategorien als Farbcode auch beim Theme-Wechsel wiedererkennbar
 * bleiben - ähnlich wie Kalender- oder Label-Farben.
 */
data class IngredientCategoryUi(val icon: ImageVector, val color: Color)

val IngredientCategory.ui: IngredientCategoryUi
    get() = when (this) {
        IngredientCategory.OBST_GEMUESE -> IngredientCategoryUi(Icons.Filled.Eco, Color(0xFF2D6A4F))
        IngredientCategory.MILCHPRODUKTE_EIER -> IngredientCategoryUi(Icons.Filled.Egg, Color(0xFFE0A22C))
        IngredientCategory.FLEISCH_FISCH -> IngredientCategoryUi(Icons.Filled.SetMeal, Color(0xFFC1272D))
        IngredientCategory.GETREIDE_BACKWAREN -> IngredientCategoryUi(Icons.Filled.BakeryDining, Color(0xFFA6784C))
        IngredientCategory.GEWUERZE_SAUCEN -> IngredientCategoryUi(Icons.Filled.Opacity, Color(0xFFD9722C))
        IngredientCategory.GETRAENKE -> IngredientCategoryUi(Icons.Filled.LocalCafe, Color(0xFF3B5BA8))
        IngredientCategory.TIEFKUEHL -> IngredientCategoryUi(Icons.Filled.AcUnit, Color(0xFF4FA6C9))
        IngredientCategory.SONSTIGES -> IngredientCategoryUi(Icons.Filled.Kitchen, Color(0xFF8A7A6D))
    }
