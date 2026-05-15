package com.g1.fidelitasapp.ui

import androidx.lifecycle.ViewModel
import com.g1.fidelitasapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(userRepository.getWelcomeMessage())
    val uiState: StateFlow<String> = _uiState
}
