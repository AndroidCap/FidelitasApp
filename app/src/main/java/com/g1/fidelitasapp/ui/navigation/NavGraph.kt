package com.g1.fidelitasapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.g1.fidelitasapp.data.storage.SessionManager
import com.g1.fidelitasapp.ui.extrato.ExtratoScreen
import com.g1.fidelitasapp.ui.extrato.ExtratoViewModel
import com.g1.fidelitasapp.ui.home.HomeScreen
import com.g1.fidelitasapp.ui.home.HomeViewModel
import com.g1.fidelitasapp.ui.login.LoginScreen
import com.g1.fidelitasapp.ui.login.LoginViewModel

// Nomes das rotas (Destinos)
object Routes {
    const val LOGIN = "login_screen"
    const val HOME = "home_screen"
    const val EXTRATO = "extrato_screen" // Nova Rota
}

@Composable
fun NavGraph(
    sessionManager: SessionManager,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        // Rota 1: Login
        composable(Routes.LOGIN) {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Rota 2: Home
        composable(Routes.HOME) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = homeViewModel,
                sessionManager = sessionManager,
                onNavigateToExtrato = {
                    // Navega para a tela de extrato
                    navController.navigate(Routes.EXTRATO)
                },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        // Rota 3: Extrato
        composable(Routes.EXTRATO) {
            val extratoViewModel: ExtratoViewModel = hiltViewModel()
            ExtratoScreen(
                viewModel = extratoViewModel,
                onNavigateBack = {
                    // Volta para a tela anterior (Home)
                    navController.popBackStack()
                }
            )
        }
    }
}
