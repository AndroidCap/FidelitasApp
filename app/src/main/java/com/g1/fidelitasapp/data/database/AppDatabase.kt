package com.g1.fidelitasapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TransactionEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}
