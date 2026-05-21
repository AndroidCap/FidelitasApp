package com.g1.fidelitasapp.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("dashboard")
    suspend fun getDashboard(
        @Header("Authorization") token: String
    ): Response<DashboardResponse>

    @GET("promocoes")
    suspend fun getPromocoes(
        @Header("Authorization") token: String
    ): Response<List<PromocaoResponse>>

    @GET("extrato")
    suspend fun getExtrato(
        @Header("Authorization") token: String
    ): Response<List<TransacaoResponse>>

    @POST("resgatar")
    suspend fun resgatar(
        @Header("Authorization") token: String,
        @Body request: ResgatarRequest
    ): Response<ResgatarResponse>

    @POST("enviar")
    suspend fun enviar(
        @Header("Authorization") token: String,
        @Body request: EnviarRequest
    ): Response<EnviarResponse>
}

