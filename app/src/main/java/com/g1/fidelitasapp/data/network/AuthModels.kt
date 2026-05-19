package com.g1.fidelitasapp.data.network

import com.google.gson.annotations.SerializedName

// Dados enviados pelo Android no req.body
data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

// Resposta recebida da API no res.json(...)
data class LoginResponse(
    @SerializedName("message") val message: String,
    @SerializedName("token") val token: String
)

// Resposta recebida do endpoint /dashboard
data class DashboardResponse(
    @SerializedName("nome") val nome: String,
    @SerializedName("saldoPontos") val saldoPontos: Int
)

// Resposta recebida do endpoint /promocoes (um item da lista de 10)
data class PromocaoResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descricao") val descricao: String,
    @SerializedName("pontos") val pontos: Int,
    @SerializedName("imageUrl") val imageUrl: String
)
// Resposta recebida do endpoint /extrato
data class TransacaoResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("descricao") val descricao: String,
    @SerializedName("pontos") val pontos: Int,
    @SerializedName("isEntrada") val isEntrada: Boolean,
    @SerializedName("dataOperacao") val dataOperacao: String
)

