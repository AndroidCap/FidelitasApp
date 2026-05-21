package com.g1.fidelitasapp.ui.home

import com.g1.fidelitasapp.data.network.PromocaoResponse

data class HomeUiState(
    val userName: String = "",
    val saldoPontos: Int = 0,
    val promocoes: List<PromocaoResponse> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val promocaoSelecionada: PromocaoResponse? = null,
    val exibirDialogo: Boolean = false,
    val isResgating: Boolean = false
)
