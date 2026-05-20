package com.g1.fidelitasapp.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.g1.fidelitasapp.data.storage.SessionManager
import com.g1.fidelitasapp.ui.catalogo.CatalogoScreen
import com.g1.fidelitasapp.ui.catalogo.CatalogoViewModel
import com.g1.fidelitasapp.ui.envio.EnviarPontosScreen
import com.g1.fidelitasapp.ui.envio.EnviarPontosViewModel
import com.g1.fidelitasapp.ui.extrato.ExtratoScreen
import com.g1.fidelitasapp.ui.extrato.ExtratoViewModel
import com.g1.fidelitasapp.ui.home.HomeScreen
import com.g1.fidelitasapp.ui.home.HomeViewModel
import com.g1.fidelitasapp.ui.login.LoginScreen
import com.g1.fidelitasapp.ui.login.LoginViewModel
import com.g1.fidelitasapp.ui.theme.BackgroundDark
import com.g1.fidelitasapp.ui.theme.PrimaryGold
import com.g1.fidelitasapp.ui.theme.SurfaceDark
import com.g1.fidelitasapp.ui.theme.TextSecondary

object Routes {
    const val LOGIN = "login_screen"
    const val HOME = "home_screen"
    const val EXTRATO = "extrato_screen"
    const val CATALOGO = "catalogo_screen"
    const val ENVIAR = "enviar_pontos_screen" // Nova Rota
}

@Composable
fun NavGraph(
    sessionManager: SessionManager,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val telasComBottomBar = listOf(Routes.HOME, Routes.EXTRATO, Routes.CATALOGO)
    val mostrarBottomBar = currentRoute in telasComBottomBar

    Scaffold(
        modifier = Modifier.fillMaxSize().background(BackgroundDark),
        bottomBar = {
            if (mostrarBottomBar) {
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    containerColor = SurfaceDark,
                    tonalElevation = 8.dp
                ) {
                    // Item 1: Home
                    NavigationBarItem(
                        selected = currentRoute == Routes.HOME,
                        onClick = {
                            if (currentRoute != Routes.HOME) {
                                navController.navigate(Routes.HOME) {
                                    popUpTo(Routes.HOME) { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryGold,
                            unselectedIconColor = TextSecondary,
                            indicatorColor = Color.Transparent
                        )
                    )

                    // Item 2: Extrato
                    NavigationBarItem(
                        selected = currentRoute == Routes.EXTRATO,
                        onClick = {
                            if (currentRoute != Routes.EXTRATO) {
                                navController.navigate(Routes.EXTRATO) {
                                    launchSingleTop = true
                                }
                            }
                        },
                        icon = { Icon(Icons.Default.List, contentDescription = "Extrato") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryGold,
                            unselectedIconColor = TextSecondary,
                            indicatorColor = Color.Transparent
                        )
                    )

                    // Item 3: Catálogo
                    NavigationBarItem(
                        selected = currentRoute == Routes.CATALOGO,
                        onClick = {
                            if (currentRoute != Routes.CATALOGO) {
                                navController.navigate(Routes.CATALOGO) {
                                    launchSingleTop = true
                                }
                            }
                        },
                        icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Catálogo") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryGold,
                            unselectedIconColor = TextSecondary,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = Modifier.padding(innerPadding)
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
                        navController.navigate(Routes.EXTRATO)
                    },
                    onNavigateToCatalogo = {
                        navController.navigate(Routes.CATALOGO)
                    },
                    onNavigateToEnviar = { // Liga com a nova rota de Envio
                        navController.navigate(Routes.ENVIAR)
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
                val homeViewModel: HomeViewModel = hiltViewModel()
                val homeUiState by homeViewModel.uiState.collectAsState()

                ExtratoScreen(
                    viewModel = extratoViewModel,
                    saldoAtual = homeUiState.saldoPontos,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Rota 4: Catálogo
            composable(Routes.CATALOGO) {
                val catalogoViewModel: CatalogoViewModel = hiltViewModel()
                CatalogoScreen(
                    viewModel = catalogoViewModel,
                    onTrocaConfirmada = { pontosGastos ->
                        // Navega de volta para a Home após sucesso na troca
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = false }
                        }
                    }
                )
            }

            // Rota 5: Enviar Pontos
            composable(Routes.ENVIAR) {
                val enviarViewModel: EnviarPontosViewModel = hiltViewModel()
                EnviarPontosScreen(
                    viewModel = enviarViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onConfirmarEnvio = { pontos, destinatario ->
                        // Navega de volta para a Home após sucesso no envio
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = false }
                        }
                    }
                )
            }
        }
    }
}