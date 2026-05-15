package com.g1.fidelitasapp.data.repository

import javax.inject.Inject
import javax.inject.Singleton

interface UserRepository {
    fun getWelcomeMessage(): String
}

@Singleton
class UserRepositoryImpl @Inject constructor() : UserRepository {
    override fun getWelcomeMessage(): String = "Bem-vindo ao Fidelitas App com Hilt e MVVM!"
}
