package com.g1.fidelitasapp.data.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val KEY_LOGIN_TIMESTAMP = longPreferencesKey("login_timestamp")
        private val KEY_TOKEN = stringPreferencesKey("jwt_token") // Nova chave de token

        private const val EXPIRATION_TIME_MS = 7L * 24L * 60L * 60L * 1000L
    }

    /**
     * Salva o status de login, o timestamp e o token JWT retornado pelo servidor.
     */
    suspend fun saveSession(token: String) {
        val currentTime = System.currentTimeMillis()
        context.dataStore.edit { preferences ->
            preferences[KEY_IS_LOGGED_IN] = true
            preferences[KEY_LOGIN_TIMESTAMP] = currentTime
            preferences[KEY_TOKEN] = token
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * Lê o token atual do DataStore (retorna String vazia se não existir).
     */
    val tokenFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_TOKEN] ?: ""
    }

    val isSessionValidFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        val isLoggedIn = preferences[KEY_IS_LOGGED_IN] ?: false
        val timestamp = preferences[KEY_LOGIN_TIMESTAMP] ?: 0L

        if (!isLoggedIn || timestamp == 0L) {
            false
        } else {
            val currentTime = System.currentTimeMillis()
            val elapsedTime = currentTime - timestamp
            elapsedTime < EXPIRATION_TIME_MS
        }
    }
}
