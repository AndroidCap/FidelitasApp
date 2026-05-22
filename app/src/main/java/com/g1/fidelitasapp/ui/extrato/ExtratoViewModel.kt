package com.g1.fidelitasapp.ui.extrato

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g1.fidelitasapp.data.repository.ExtratoRepository
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

@HiltViewModel
class ExtratoViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val extratoRepository: ExtratoRepository,
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExtratoUiState())
    val uiState: StateFlow<ExtratoUiState> = _uiState.asStateFlow()

    init {
        observeLocalDatabase()
        observeSaldo()
        refreshExtrato()
    }

    private fun observeSaldo() {
        viewModelScope.launch {
            homeRepository.saldoFlow.collect { novoSaldo ->
                _uiState.update { it.copy(saldoPontos = novoSaldo) }
            }
        }
    }

    private fun observeLocalDatabase() {
        viewModelScope.launch {
            extratoRepository.localTransactionsFlow.collect { dbTransactions ->
                _uiState.update { it.copy(transacoes = dbTransactions) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun refreshExtrato() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val token = sessionManager.tokenFlow.first()
            if (token.isEmpty()) {
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            val transactionsResult = extratoRepository.refreshTransactions(token)
            homeRepository.fetchDashboard(token)

            transactionsResult.fold(
                onSuccess = {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            successMessage = "Extrato atualizado!"
                        ) 
                    }
                },
                onFailure = { error ->
                    if (sessionManager.tokenFlow.first().isNotEmpty()) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Erro ao carregar extrato."
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
            )
        }
    }
}