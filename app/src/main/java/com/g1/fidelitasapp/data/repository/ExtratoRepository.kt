package com.g1.fidelitasapp.data.repository

import com.g1.fidelitasapp.data.database.TransactionDao
import com.g1.fidelitasapp.data.database.TransactionEntity
import com.g1.fidelitasapp.data.network.ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExtratoRepository @Inject constructor(
    private val apiService: ApiService,
    private val transactionDao: TransactionDao
) {
    // Retorna a lista de transações salva localmente no banco Room
    val localTransactionsFlow: Flow<List<TransactionEntity>> = transactionDao.getAllTransactionsFlow()

    /**
     * Busca as transações da API Node.js e salva no Room.
     * Se a rede falhar, retorna um erro, mas a UI ainda exibirá os dados salvos localmente.
     */
    suspend fun refreshTransactions(token: String): Result<Unit> {
        return try {
            val formattedToken = "Bearer $token"
            val response = apiService.getExtrato(formattedToken)

            if (response.isSuccessful && response.body() != null) {
                val networkTransacoes = response.body()!!

                // Mapeia os dados da rede (TransacaoResponse) para a entidade do banco local (TransactionEntity)
                val localEntities = networkTransacoes.map { networkItem ->
                    TransactionEntity(
                        id = networkItem.id,
                        descricao = networkItem.descricao,
                        pontos = networkItem.pontos,
                        isEntrada = networkItem.isEntrada,
                        dataOperacao = networkItem.dataOperacao
                    )
                }

                // Limpa e atualiza o banco local com os dados mais frescos da API
                transactionDao.clearAllTransactions()
                transactionDao.insertTransactions(localEntities)

                Result.success(Unit)
            } else {
                Result.failure(Exception("Falha ao obter extrato da API"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
