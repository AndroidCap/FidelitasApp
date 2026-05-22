package com.g1.fidelitasapp.data.repository

import com.g1.fidelitasapp.data.database.TransactionDao
import com.g1.fidelitasapp.data.database.TransactionEntity
import com.g1.fidelitasapp.data.network.ApiService
import com.g1.fidelitasapp.data.network.DashboardResponse
import com.g1.fidelitasapp.data.network.PromocaoResponse
import com.g1.fidelitasapp.data.network.ResgatarRequest
import com.g1.fidelitasapp.data.network.EnviarRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val apiService: ApiService,
    private val transactionDao: TransactionDao
) {
    private val _saldoFlow = MutableStateFlow(0)
    val saldoFlow: StateFlow<Int> = _saldoFlow.asStateFlow()

    suspend fun fetchDashboard(token: String): Result<DashboardResponse> {
        return try {
            val response = apiService.getDashboard("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                _saldoFlow.value = data.saldoPontos
                Result.success(data)
            } else {
                Result.failure(Exception("Erro ao carregar dados do usuário"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchPromocoes(token: String): Result<List<PromocaoResponse>> {
        return try {
            val response = apiService.getPromocoes("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erro ao carregar promoções"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resgatar(token: String, promocaoId: Int, pontos: Int, titulo: String): Result<Int> {
        return try {
            val response = apiService.resgatar("Bearer $token", ResgatarRequest(promocaoId, pontos, titulo))
            val body = response.body()
            if (response.isSuccessful && body != null) {
                transactionDao.insertTransaction(
                    TransactionEntity(
                        id = body.transacao.id,
                        descricao = body.transacao.descricao,
                        pontos = body.transacao.pontos,
                        isEntrada = body.transacao.isEntrada,
                        dataOperacao = body.transacao.dataOperacao
                    )
                )
                _saldoFlow.value = body.novoSaldo
                Result.success(body.novoSaldo)
            } else {
                val msg = if (response.code() == 400) "Saldo insuficiente." else "Erro ao processar resgate."
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun enviar(token: String, pontos: Int, destinatario: String): Result<Int> {
        return try {
            val response = apiService.enviar("Bearer $token", EnviarRequest(pontos, destinatario))
            val body = response.body()
            if (response.isSuccessful && body != null) {
                // Sincroniza banco local
                transactionDao.insertTransaction(
                    TransactionEntity(
                        id = body.transacao.id,
                        descricao = body.transacao.descricao,
                        pontos = body.transacao.pontos,
                        isEntrada = body.transacao.isEntrada,
                        dataOperacao = body.transacao.dataOperacao
                    )
                )
                _saldoFlow.value = body.novoSaldo
                Result.success(body.novoSaldo)
            } else {
                // Trata especificamente o erro 404 (usuário inexistente)
                val errorMsg = when (response.code()) {
                    404 -> "Destinatário não encontrado."
                    400 -> "Dados inválidos ou saldo insuficiente."
                    401 -> "Sessão expirada."
                    else -> "Erro ao enviar pontos (${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Falha na conexão: ${e.message}"))
        }
    }
}