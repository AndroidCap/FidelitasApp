package com.g1.fidelitasapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g1.fidelitasapp.data.repository.AuthRepository
import com.g1.fidelitasapp.data.storage.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository // Repositório da API real injetado
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        checkExistingSession()
    }

    private fun checkExistingSession() {
        viewModelScope.launch {
            sessionManager.isSessionValidFlow.collect { isValid ->
                if (isValid) {
                    _uiState.update { it.copy(isSuccess = true) }
                }
            }
        }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun onRememberMeChange(rememberMe: Boolean) {
        _uiState.update { it.copy(rememberMe = rememberMe) }
    }

    fun login() {
        val currentState = _uiState.value

        if (!currentState.email.contains("@") || !currentState.email.contains(".")) {
            _uiState.update { it.copy(errorMessage = "Por favor, insira um e-mail válido.") }
            return
        }

        if (currentState.password.length < 6) {
            _uiState.update { it.copy(errorMessage = "A senha deve ter no mínimo 6 caracteres.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Chama a API real construída em Node.js
            val result = authRepository.authenticate(currentState.email, currentState.password)

            result.fold(
                onSuccess = { token ->
                    // Login bem-sucedido!
                    if (currentState.rememberMe) {
                        sessionManager.saveSession()
                    }
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                },
                onFailure = { exception ->
                    // Exibe a mensagem de erro (ex: "Usuário ou senha inválidos")
                    _uiState.update { it.copy(isLoading = false, errorMessage = exception.message) }
                }
            )
        }
    }
}
