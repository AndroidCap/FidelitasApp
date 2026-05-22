package com.g1.fidelitasapp.ui.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.g1.fidelitasapp.data.storage.SessionManager
import com.g1.fidelitasapp.ui.components.GlassmorphismDialog
import com.g1.fidelitasapp.ui.theme.AccentGold
import com.g1.fidelitasapp.ui.theme.BackgroundDark
import com.g1.fidelitasapp.ui.theme.PrimaryGold
import com.g1.fidelitasapp.ui.theme.SurfaceDark
import com.g1.fidelitasapp.ui.theme.SurfaceDarkElevated
import com.g1.fidelitasapp.ui.theme.TextPrimary
import com.g1.fidelitasapp.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    sessionManager: SessionManager,
    onNavigateToExtrato: () -> Unit,
    onNavigateToCatalogo: () -> Unit,
    onNavigateToEnviar: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
        uiState.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Olá,", color = TextSecondary, fontSize = 14.sp)
                        Text(text = uiState.userName, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadHomeData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Recarregar", tint = PrimaryGold)
                    }
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                sessionManager.clearSession()
                                onLogout()
                            }
                        },
                        modifier = Modifier.clip(CircleShape).background(SurfaceDark)
                    ) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Desconectar", tint = Color(0xFFCF6679))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (uiState.isLoading && uiState.userName.isEmpty()) {
                CircularProgressIndicator(color = PrimaryGold, modifier = Modifier.align(Alignment.Center))
            } else {
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 24.dp)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(SurfaceDark.copy(alpha = 0.9f))
                            .border(1.5.dp, PrimaryGold.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                            .padding(24.dp)
                    ) {
                        Text(text = "SALDO ACUMULADO", color = AccentGold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.5.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = String.format(Locale.getDefault(), "%,d", uiState.saldoPontos).replace(",", "."),
                                    color = PrimaryGold, fontSize = 42.sp, fontWeight = FontWeight.Bold
                                )
                                Text(text = " pts", color = AccentGold, fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 8.dp))
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(SurfaceDarkElevated).padding(horizontal = 12.dp, vertical = 6.dp).clickable { onNavigateToExtrato() }
                            ) {
                                Text(text = "Extrato", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    Text(text = "AÇÕES RÁPIDAS", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp, modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp))
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                        HomeShortcutItem(icon = Icons.Default.ShoppingCart, label = "Trocar Pontos", onClick = { onNavigateToCatalogo() })
                        HomeShortcutItem(icon = Icons.Default.Send, label = "Enviar", onClick = { onNavigateToEnviar() })
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(text = "DESTAQUES DA SEMANA", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp, modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp))
                    LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(uiState.promocoes) { promocao ->
                            PromocaoCard(promocao = promocao, onClick = { viewModel.selecionarPromocao(promocao) })
                        }
                    }
                }
            }
            if (uiState.exibirDialogo && uiState.promocaoSelecionada != null) {
                GlassmorphismDialog(
                    titulo = "Confirmar Resgate",
                    mensagem = "Deseja trocar ${uiState.promocaoSelecionada!!.pontos} pontos por: ${uiState.promocaoSelecionada!!.titulo}?",
                    textoConfirmar = "Resgatar",
                    onDismissRequest = { viewModel.fecharDialogo() },
                    onConfirm = { viewModel.resgatar() }
                )
            }
        }
    }
}

@Composable
fun HomeShortcutItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(SurfaceDark).border(1.dp, PrimaryGold.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = label, tint = PrimaryGold, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun PromocaoCard(promocao: com.g1.fidelitasapp.data.network.PromocaoResponse, onClick: () -> Unit) {
    Column(modifier = Modifier.width(260.dp).clip(RoundedCornerShape(16.dp)).background(SurfaceDark).border(1.dp, SurfaceDarkElevated, RoundedCornerShape(16.dp)).clickable { onClick() }) {
        AsyncImage(model = promocao.imageUrl, contentDescription = promocao.titulo, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().height(140.dp))
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = promocao.titulo, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = promocao.descricao, color = TextSecondary, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.height(32.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "A partir de", color = TextSecondary, fontSize = 11.sp)
                Text(text = "${promocao.pontos} pts", color = PrimaryGold, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
