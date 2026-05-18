package com.g1.fidelitasapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g1.fidelitasapp.data.storage.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        checkExistingSession()
    }

    /**
     * Verifica imediatamente no DataStore se já existe uma sessão de 7 dias válida.
     */
    private fun checkExistingSession() {
        viewModelScope.launch {
            sessionManager.isSessionValidFlow.collect { isValid ->
                if (isValid) {
                    // Se a sessão for válida, sinaliza para o NavGraph saltar para a Home
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

    /**
     * Valida os campos e simula o processo de login com a API.
     */
    fun login() {
        val currentState = _uiState.value

        // Validação de E-mail (Requisito de UI)
        if (!currentState.email.contains("@") || !currentState.email.contains(".")) {
            _uiState.update { it.copy(errorMessage = "Por favor, insira um e-mail válido.") }
            return
        }

        // Validação de Senha
        if (currentState.password.length < 6) {
            _uiState.update { it.copy(errorMessage = "A senha deve ter no mínimo 6 caracteres.") }
            return
        }

        // Inicia o processo de login (mostra indicador de carregamento)
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Simula tempo de resposta de API (1.5 segundos)
            delay(1500)

            // Se o usuário marcou "Lembrar de mim", salva a data e status no DataStore
            if (currentState.rememberMe) {
                sessionManager.saveSession()
            }

            // Avisa a tela que o login teve sucesso
            _uiState.update { it.copy(isLoading = false, isSuccess = true) }
        }
    }
}
