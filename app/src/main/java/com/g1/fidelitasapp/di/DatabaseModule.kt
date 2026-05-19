package com.g1.fidelitasapp.di

import android.content.Context
import androidx.room.Room
import com.g1.fidelitasapp.data.database.AppDatabase
import com.g1.fidelitasapp.data.database.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "fidelitas_database"
        ).fallbackToDestructiveMigration() // Em caso de mudança na estrutura do banco, recria as tabelas sem travar o app
            .build()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(database: AppDatabase): TransactionDao {
        return database.transactionDao()
    }
}
