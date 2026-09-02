package com.example.whatsinmyfridge.application.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsinmyfridge.domain.model.ThemeMode
import com.example.whatsinmyfridge.domain.usecase.ObserveUserProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ThemeViewModel(observeUserProfile: ObserveUserProfileUseCase) : ViewModel() {

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    init {
        viewModelScope.launch {
            observeUserProfile().collect { profile ->
                _themeMode.update { profile?.themeMode ?: ThemeMode.SYSTEM }
            }
        }
    }
}