package com.g1.fidelitasapp.data.repository

import com.g1.fidelitasapp.data.network.ApiService
import com.g1.fidelitasapp.data.network.LoginRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService
) {
    /**
     * Tenta fazer o login e retorna um objeto Result (Sucesso com Token ou Falha com Erro).
     */
    suspend fun authenticate(email: String, password: String): Result<String> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                // Se o servidor retornou 200, pegamos o token retornado
                Result.success(response.body()!!.token)
            } else {
                // Se retornou 401 (Usuário ou senha inválidos)
                Result.failure(Exception("Usuário ou senha inválidos"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Falha ao conectar no servidor. Verifique se a API está rodando."))
        }
    }
}
