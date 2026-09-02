package com.example.whatsinmyfridge.presentation.common

import com.example.whatsinmyfridge.domain.model.Allergy
import com.example.whatsinmyfridge.domain.model.DietType
import com.example.whatsinmyfridge.domain.model.ThemeMode

fun DietType.label(): String = when (this) {
    DietType.OMNIVORE -> "Alles"
    DietType.VEGETARIAN -> "Vegetarisch"
    DietType.VEGAN -> "Vegan"
    DietType.PESCETARIAN -> "Pescetarisch"
}

fun ThemeMode.label(): String = when (this) {
    ThemeMode.SYSTEM -> "System"
    ThemeMode.LIGHT -> "Hell"
    ThemeMode.DARK -> "Dunkel"
    ThemeMode.PLAYFUL -> "Verspielt"
    ThemeMode.SUNSET -> "Sonnenuntergang"
    ThemeMode.BERRY -> "Beere"
    ThemeMode.SAGE -> "Salbei"
    ThemeMode.POMEGRANATE -> "Granatapfel"
    ThemeMode.CITRUS -> "Zitrus"
    ThemeMode.MIDNIGHT -> "Mitternacht"
    ThemeMode.KONTRAST -> "Kontrast"
}

fun Allergy.label(): String = when (this) {
    Allergy.GLUTEN -> "Gluten"
    Allergy.LACTOSE -> "Laktose"
    Allergy.NUTS -> "Nüsse"
    Allergy.EGGS -> "Eier"
    Allergy.SOY -> "Soja"
}
