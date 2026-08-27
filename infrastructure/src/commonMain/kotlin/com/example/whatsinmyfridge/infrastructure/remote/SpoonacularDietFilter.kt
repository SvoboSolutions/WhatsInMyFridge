package com.example.whatsinmyfridge.infrastructure.remote

import com.example.whatsinmyfridge.domain.model.Allergy
import com.example.whatsinmyfridge.domain.model.DietType
import com.example.whatsinmyfridge.infrastructure.remote.dto.SpoonacularRecipeDetailsDto

/**
 * Spoonacular liefert nur für einige Ernährungsweisen/Unverträglichkeiten strukturierte
 * Flags (vegetarian/vegan/glutenFree/dairyFree). Für Nüsse/Eier/Soja gibt es keine Flags im
 * Basis-Tarif, deshalb hier ein Best-Effort-Fallback über die Zutatentexte. Kein Ersatz für
 * echte medizinische Allergie-Sicherheit, aber deutlich besser als kein Filter.
 */
fun SpoonacularRecipeDetailsDto.matchesDiet(dietType: DietType): Boolean = when (dietType) {
    DietType.OMNIVORE -> true
    DietType.VEGETARIAN -> vegetarian
    DietType.VEGAN -> vegan
    // Spoonacular kennt kein "pescetarian"-Flag im Basis-Tarif - bewusst nicht filtern,
    // statt fälschlich Treffer auszuschließen.
    DietType.PESCETARIAN -> true
}

fun SpoonacularRecipeDetailsDto.matchesAllergies(allergies: Set<Allergy>): Boolean {
    if (allergies.isEmpty()) return true
    val ingredientText = extendedIngredients.joinToString(" ") { it.original.lowercase() }

    return allergies.all { allergy ->
        when (allergy) {
            Allergy.GLUTEN -> glutenFree
            Allergy.LACTOSE -> dairyFree
            Allergy.NUTS -> NUT_KEYWORDS.none { it in ingredientText }
            Allergy.EGGS -> EGG_KEYWORDS.none { it in ingredientText }
            Allergy.SOY -> SOY_KEYWORDS.none { it in ingredientText }
        }
    }
}

private val NUT_KEYWORDS = listOf("nut", "peanut", "almond", "cashew", "hazelnut", "pistachio", "walnut")
private val EGG_KEYWORDS = listOf("egg")
private val SOY_KEYWORDS = listOf("soy", "tofu", "edamame")
