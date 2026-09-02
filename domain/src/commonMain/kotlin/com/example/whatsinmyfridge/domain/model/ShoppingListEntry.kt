package com.example.whatsinmyfridge.domain.model

import kotlinx.datetime.LocalDate

/** Fehlende Zutaten für ein geplantes Rezept - eine Gruppe pro Tag/Rezept auf der Einkaufsliste. */
data class ShoppingListEntry(
    val date: LocalDate,
    val recipeTitle: String,
    val missingIngredients: List<String>,
)
