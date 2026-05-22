package com.g1.fidelitasapp.data.network

import com.g1.fidelitasapp.data.storage.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val sessionManagerProvider: Provider<SessionManager>
) : Interceptor {
    
    // Escopo para operações de limpeza assíncronas
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        val isLoginRequest = request.url.encodedPath.contains("login")
        
        if (response.code == 401 && !isLoginRequest) {
            // Executa a limpeza sem bloquear a thread do OkHttp (evita deadlock)
            scope.launch {
                try {
                    sessionManagerProvider.get().clearSession()
                } catch (e: Exception) {
                    // Log de erro silencioso
                }
            }
        }
        return response
    }
}