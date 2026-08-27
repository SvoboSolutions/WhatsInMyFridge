package com.example.whatsinmyfridge.presentation.mealplan

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

private val germanWeekdayShort = mapOf(
    DayOfWeek.MONDAY to "Mo",
    DayOfWeek.TUESDAY to "Di",
    DayOfWeek.WEDNESDAY to "Mi",
    DayOfWeek.THURSDAY to "Do",
    DayOfWeek.FRIDAY to "Fr",
    DayOfWeek.SATURDAY to "Sa",
    DayOfWeek.SUNDAY to "So",
)

fun LocalDate.toWeekdayShort(): String = germanWeekdayShort.getValue(dayOfWeek)

@Suppress("DEPRECATION")
fun LocalDate.toDayMonth(): String = "${dayOfMonth.toString().padStart(2, '0')}.${monthNumber.toString().padStart(2, '0')}."
