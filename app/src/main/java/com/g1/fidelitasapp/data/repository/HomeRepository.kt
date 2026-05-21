package com.g1.fidelitasapp.data.repository

import com.g1.fidelitasapp.data.database.TransactionDao
import com.g1.fidelitasapp.data.database.TransactionEntity
import com.g1.fidelitasapp.data.network.ApiService
import com.g1.fidelitasapp.data.network.DashboardResponse
import com.g1.fidelitasapp.data.network.PromocaoResponse
import com.g1.fidelitasapp.data.network.ResgatarRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val apiService: ApiService,
    private val transactionDao: TransactionDao
) {
    /**
     * Carrega as informações do Dashboard (Nome e Saldo de Pontos) da API.
     */
    suspend fun fetchDashboard(token: String): Result<DashboardResponse> {
        return try {
            val formattedToken = "Bearer $token"
            val response = apiService.getDashboard(formattedToken)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erro ao carregar dados do usuário"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Carrega as 10 Recompensas/Promoções da API.
     */
    suspend fun fetchPromocoes(token: String): Result<List<PromocaoResponse>> {
        return try {
            val formattedToken = "Bearer $token"
            val response = apiService.getPromocoes(formattedToken)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erro ao carregar promoções"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Envia o resgate de uma promoção para a API, deduz o saldo e
     * salva a nova transação no banco local Room.
     * Retorna o novo saldo em caso de sucesso.
     */
    suspend fun resgatar(token: String, promocaoId: Int, pontos: Int, titulo: String): Result<Int> {
        return try {
            val formattedToken = "Bearer $token"
            val response = apiService.resgatar(formattedToken, ResgatarRequest(promocaoId, pontos, titulo))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                transactionDao.insertTransaction(
                    TransactionEntity(
                        id = body.transacao.id,
                        descricao = body.transacao.descricao,
                        pontos = body.transacao.pontos,
                        isEntrada = body.transacao.isEntrada,
                        dataOperacao = body.transacao.dataOperacao
                    )
                )
                Result.success(body.novoSaldo)
            } else {
                val msg = if (response.code() == 400) "Saldo insuficiente para este resgate." else "Erro ao processar resgate."
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
