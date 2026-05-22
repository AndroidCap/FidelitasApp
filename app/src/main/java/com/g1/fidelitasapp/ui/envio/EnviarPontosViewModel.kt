package com.g1.fidelitasapp.ui.envio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g1.fidelitasapp.data.repository.HomeRepository
import com.g1.fidelitasapp.data.storage.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EnviarPontosUiState(
    val isLoading: Boolean = false,
    val destinatario: String = "",
    val pontos: String = "",
    val saldoDisponivel: Int = 0,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isSucesso: Boolean = false
)

@HiltViewModel
class EnviarPontosViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EnviarPontosUiState())
    val uiState: StateFlow<EnviarPontosUiState> = _uiState.asStateFlow()

    init {
        carregarSaldoInicial()
        observeSaldo()
    }

    private fun observeSaldo() {
        viewModelScope.launch {
            homeRepository.saldoFlow.collect { novoSaldo ->
                _uiState.update { it.copy(saldoDisponivel = novoSaldo) }
            }
        }
    }

    private fun carregarSaldoInicial() {
        viewModelScope.launch {
            val token = sessionManager.tokenFlow.first()
            if (token.isNotEmpty()) {
                homeRepository.fetchDashboard(token)
            }
        }
    }

    fun onDestinatarioChanged(novoDestinatario: String) {
        _uiState.update { it.copy(destinatario = novoDestinatario, errorMessage = null) }
    }

    fun onPontosChanged(novosPontos: String) {
        if (novosPontos.all { it.isDigit() }) {
            _uiState.update { it.copy(pontos = novosPontos, errorMessage = null) }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun processarEnvio(onSucesso: () -> Unit) {
        val state = _uiState.value
        val pontosInt = state.pontos.toIntOrNull() ?: 0

        if (state.destinatario.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Digite o destinatário.") }
            return
        }
        if (pontosInt <= 0) {
            _uiState.update { it.copy(errorMessage = "Pontos inválidos.") }
            return
        }
        if (pontosInt > state.saldoDisponivel) {
            _uiState.update { it.copy(errorMessage = "Saldo insuficiente.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val token = sessionManager.tokenFlow.first()
                val result = homeRepository.enviar(token, pontosInt, state.destinatario)
                
                result.fold(
                    onSuccess = {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isSucesso = true,
                                successMessage = "Pontos enviados com sucesso!",
                                destinatario = "",
                                pontos = ""
                            )
                        }
                        onSucesso()
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Erro ao enviar pontos."
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Falha na operação: ${e.message}"
                    )
                }
            }
        }
    }
}
