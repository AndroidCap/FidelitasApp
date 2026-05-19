package com.g1.fidelitasapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    // Retorna um Flow com a lista de transações ordenadas do ID maior para o menor (mais recentes primeiro)
    @Query("SELECT * FROM transactions ORDER BY id DESC")
    fun getAllTransactionsFlow(): Flow<List<TransactionEntity>>

    // Insere uma lista de transações (usado para salvar o cache da API localmente)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    // Insere uma única transação (usado quando fizermos um novo envio ou resgate de pontos no app)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions")
    suspend fun clearAllTransactions()
}
