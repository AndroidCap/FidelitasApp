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
        carregarSaldo()
    }

    private fun carregarSaldo() {
        viewModelScope.launch {
            val token = sessionManager.tokenFlow.first()
            if (token.isNotEmpty()) {
                val result = homeRepository.fetchDashboard(token)
                if (result.isSuccess) {
                    _uiState.update { it.copy(saldoDisponivel = result.getOrThrow().saldoPontos) }
                }
            }
        }
    }

    fun onDestinatarioChanged(novoDestinatario: String) {
        _uiState.update { it.copy(destinatario = novoDestinatario, errorMessage = null) }
    }

    fun onPontosChanged(novosPontos: String) {
        // Aceita apenas números
        if (novosPontos.all { it.isDigit() }) {
            _uiState.update { it.copy(pontos = novosPontos, errorMessage = null) }
        }
    }

    fun processarEnvio(onSucesso: () -> Unit) {
        val state = _uiState.value
        val pontosInt = state.pontos.toIntOrNull() ?: 0

        // Validações
        if (state.destinatario.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Digite o e-mail ou CPF do destinatário.") }
            return
        }
        if (pontosInt <= 0) {
            _uiState.update { it.copy(errorMessage = "A quantidade de pontos deve ser maior que zero.") }
            return
        }
        if (pontosInt > state.saldoDisponivel) {
            _uiState.update { it.copy(errorMessage = "Saldo insuficiente para esta operação.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Simula delay de rede da transferência
            kotlinx.coroutines.delay(1500)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isSucesso = true,
                    saldoDisponivel = state.saldoDisponivel - pontosInt
                )
            }
            onSucesso()
        }
    }

    fun resetState() {
        _uiState.update {
            it.copy(
                destinatario = "",
                pontos = "",
                isSucesso = false,
                errorMessage = null
            )
        }
    }
}