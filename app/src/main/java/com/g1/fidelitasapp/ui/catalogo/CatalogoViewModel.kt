package com.g1.fidelitasapp.ui.catalogo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g1.fidelitasapp.data.network.PromocaoResponse
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

data class CatalogoUiState(
    val isLoading: Boolean = false,
    val premios: List<PromocaoResponse> = emptyList(),
    val saldoPontos: Int = 0,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class CatalogoViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val repository: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogoUiState())
    val uiState: StateFlow<CatalogoUiState> = _uiState.asStateFlow()

    init {
        loadPremios()
        observeSaldo()
    }

    private fun observeSaldo() {
        viewModelScope.launch {
            repository.saldoFlow.collect { novoSaldo ->
                _uiState.update { it.copy(saldoPontos = novoSaldo) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun loadPremios() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val token = sessionManager.tokenFlow.first()
            if (token.isEmpty()) {
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            repository.fetchDashboard(token)

            val result = repository.fetchPromocoes(token)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        premios = result.getOrThrow()
                    )
                }
            } else {
                if (sessionManager.tokenFlow.first().isNotEmpty()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.exceptionOrNull()?.message ?: "Erro ao carregar prêmios"
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun resgatar(promocao: PromocaoResponse, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val token = sessionManager.tokenFlow.first()
            val result = repository.resgatar(token, promocao.id, promocao.pontos, promocao.titulo)
            
            result.fold(
                onSuccess = {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            successMessage = "Resgate de '${promocao.titulo}' realizado com sucesso!"
                        ) 
                    }
                    onSuccess()
                },
                onFailure = { error ->
                    if (sessionManager.tokenFlow.first().isNotEmpty()) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Erro ao resgatar"
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