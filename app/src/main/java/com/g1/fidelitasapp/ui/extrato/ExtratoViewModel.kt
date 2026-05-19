package com.g1.fidelitasapp.ui.extrato

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g1.fidelitasapp.data.repository.ExtratoRepository
import com.g1.fidelitasapp.data.storage.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExtratoViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val extratoRepository: ExtratoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExtratoUiState())
    val uiState: StateFlow<ExtratoUiState> = _uiState.asStateFlow()

    init {
        observeLocalDatabase()
        refreshExtrato()
    }

    /**
     * Observa a tabela local do Room em tempo real. Qualquer mudança no banco
     * atualiza a nossa UI instantaneamente, mesmo offline.
     */
    private fun observeLocalDatabase() {
        viewModelScope.launch {
            extratoRepository.localTransactionsFlow.collect { dbTransactions ->
                _uiState.update { it.copy(transacoes = dbTransactions) }
            }
        }
    }

    /**
     * Busca dados frescos da API e atualiza o banco local do Room.
     */
    fun refreshExtrato() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val token = sessionManager.tokenFlow.first()
            if (token.isEmpty()) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Sessão inválida.") }
                return@launch
            }

            val result = extratoRepository.refreshTransactions(token)

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false) }
                },
                onFailure = { error ->
                    // Mesmo se houver falha de internet/conexão, o banco local do Room
                    // continuará ativo exibindo o cache. Apenas sinalizamos o aviso se o banco estiver vazio.
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = if (it.transacoes.isEmpty()) "Falha ao conectar no servidor." else null
                        )
                    }
                }
            )
        }
    }
}
