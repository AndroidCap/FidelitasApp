package com.g1.fidelitasapp.ui.extrato

import com.g1.fidelitasapp.data.database.TransactionEntity

data class ExtratoUiState(
    val transacoes: List<TransactionEntity> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
