package com.example.whatsinmyfridge.presentation.common

import com.example.whatsinmyfridge.domain.model.Allergy
import com.example.whatsinmyfridge.domain.model.DietType

fun DietType.label(): String = when (this) {
    DietType.OMNIVORE -> "Alles"
    DietType.VEGETARIAN -> "Vegetarisch"
    DietType.VEGAN -> "Vegan"
    DietType.PESCETARIAN -> "Pescetarisch"
}

fun Allergy.label(): String = when (this) {
    Allergy.GLUTEN -> "Gluten"
    Allergy.LACTOSE -> "Laktose"
    Allergy.NUTS -> "Nüsse"
    Allergy.EGGS -> "Eier"
    Allergy.SOY -> "Soja"
}
