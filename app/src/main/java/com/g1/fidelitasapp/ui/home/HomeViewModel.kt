package com.g1.fidelitasapp.ui.home

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

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    /**
     * Carrega as informações necessárias da API usando o token JWT salvo
     */
    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Pega o token salvo no DataStore
            val token = sessionManager.tokenFlow.first()

            if (token.isEmpty()) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Sessão inválida. Por favor, logue novamente.") }
                return@launch
            }

            // Busca os dados da API em paralelo
            val dashboardResult = homeRepository.fetchDashboard(token)
            val promocoesResult = homeRepository.fetchPromocoes(token)

            if (dashboardResult.isSuccess && promocoesResult.isSuccess) {
                val dashboardData = dashboardResult.getOrThrow()
                val promocoesList = promocoesResult.getOrThrow()

                _uiState.update {
                    it.copy(
                        userName = dashboardData.nome,
                        saldoPontos = dashboardData.saldoPontos,
                        promocoes = promocoesList,
                        isLoading = false
                    )
                }
            } else {
                val errorMsg = dashboardResult.exceptionOrNull()?.message
                    ?: promocoesResult.exceptionOrNull()?.message
                    ?: "Erro desconhecido ao carregar dados"

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = errorMsg
                    )
                }
            }
        }
    }
}
