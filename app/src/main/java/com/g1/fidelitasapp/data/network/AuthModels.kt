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
