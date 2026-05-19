package com.g1.fidelitasapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val descricao: String,
    val pontos: Int,
    val isEntrada: Boolean, // true = pontos ganhos (+), false = pontos resgatados (-)
    val dataOperacao: String // Ex: "19 Mai 2026"
)
