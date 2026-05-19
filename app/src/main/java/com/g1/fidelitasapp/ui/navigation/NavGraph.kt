package com.g1.fidelitasapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.g1.fidelitasapp.data.storage.SessionManager
import com.g1.fidelitasapp.ui.home.HomeScreen
import com.g1.fidelitasapp.ui.home.HomeViewModel
import com.g1.fidelitasapp.ui.login.LoginScreen
import com.g1.fidelitasapp.ui.login.LoginViewModel

// Nomes das rotas (Destinos)
object Routes {
    const val LOGIN = "login_screen"
    const val HOME = "home_screen"
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
                    // Ao navegar para a Home, removemos o Login da pilha de retorno
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Rota 2: Home
        composable(Routes.HOME) {
            val homeViewModel: HomeViewModel = hiltViewModel() // Instancia o ViewModel da Home usando Hilt
            HomeScreen(
                viewModel = homeViewModel,
                sessionManager = sessionManager,
                onLogout = {
                    // Ao sair, volta para o Login e limpa a pilha de retorno
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }
    }
}
