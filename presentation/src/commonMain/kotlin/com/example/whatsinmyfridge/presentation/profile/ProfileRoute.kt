package com.example.whatsinmyfridge.presentation.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.whatsinmyfridge.application.profile.ProfileViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileRoute(viewModel: ProfileViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    ProfileScreen(state = state, onIntent = viewModel::onIntent)
}
