package com.g1.fidelitasapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.g1.fidelitasapp.data.storage.SessionManager
import com.g1.fidelitasapp.ui.navigation.NavGraph
import com.g1.fidelitasapp.ui.theme.FidelitasAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // Inicializa a Splash Screen API
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FidelitasAppTheme {
                NavGraph(sessionManager = sessionManager)
            }
        }
    }
}
