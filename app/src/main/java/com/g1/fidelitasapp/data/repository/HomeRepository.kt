package com.g1.fidelitasapp.data.repository

import com.g1.fidelitasapp.data.network.ApiService
import com.g1.fidelitasapp.data.network.DashboardResponse
import com.g1.fidelitasapp.data.network.PromocaoResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val apiService: ApiService
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
}
