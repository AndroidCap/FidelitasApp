package com.g1.fidelitasapp.data.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Cria a instância única do arquivo DataStore chamado "user_session.preferences_pb"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Definição das chaves (Keys) no DataStore
    companion object {
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val KEY_LOGIN_TIMESTAMP = longPreferencesKey("login_timestamp")

        // 7 dias calculados em milissegundos: 7 * 24 * 60 * 60 * 1000L
        // 1 dia = teste
        private const val EXPIRATION_TIME_MS = 1L * 24L * 60L * 60L * 1000L
    }

    /**
     * Salva que o usuário marcou "Lembrar de mim" e grava o instante atual.
     */
    suspend fun saveSession() {
        val currentTime = System.currentTimeMillis()
        context.dataStore.edit { preferences ->
            preferences[KEY_IS_LOGGED_IN] = true
            preferences[KEY_LOGIN_TIMESTAMP] = currentTime
        }
    }

    /**
     * Limpa a sessão ativa (usado no botão Sair ou quando expira).
     */
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * Retorna um Flow contendo 'true' se o usuário está logado e o tempo de 7 dias não expirou.
     */
    val isSessionValidFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        val isLoggedIn = preferences[KEY_IS_LOGGED_IN] ?: false
        val timestamp = preferences[KEY_LOGIN_TIMESTAMP] ?: 0L

        if (!isLoggedIn || timestamp == 0L) {
            false
        } else {
            val currentTime = System.currentTimeMillis()
            val elapsedTime = currentTime - timestamp
            // Retorna true somente se não tiver ultrapassado os 7 dias
            elapsedTime < EXPIRATION_TIME_MS
        }
    }
}
