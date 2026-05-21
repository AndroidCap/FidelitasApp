package com.g1.fidelitasapp.ui.catalogo

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.g1.fidelitasapp.data.network.PromocaoResponse
import com.g1.fidelitasapp.ui.theme.BackgroundDark
import com.g1.fidelitasapp.ui.theme.PrimaryGold
import com.g1.fidelitasapp.ui.theme.SurfaceDark
import com.g1.fidelitasapp.ui.theme.SurfaceDarkElevated
import com.g1.fidelitasapp.ui.theme.TextPrimary
import com.g1.fidelitasapp.ui.theme.TextSecondary
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.g1.fidelitasapp.ui.components.GlassmorphismDialog

@Composable
fun CatalogoScreen(
    viewModel: CatalogoViewModel,
    onTrocaConfirmada: (pontosGastos: Int) -> Unit // Adicionado para atualizar o saldo
) {
    val uiState by viewModel.uiState.collectAsState()
    // Estados locais para controlar a exibição do diálogo e o item selecionado
    var exibirDialogo by remember { mutableStateOf(false) }
    var premioSelecionado by remember { mutableStateOf<PromocaoResponse?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Catálogo de Prêmios",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(start = 8.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { viewModel.loadPremios() }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Recarregar",
                    tint = PrimaryGold
                )
            }
        }

        // Conteúdo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = PrimaryGold,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (uiState.errorMessage != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = Color.Red,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // No LazyVerticalGrid, troque a ação do clique para ativar o diálogo:
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.premios) { premio ->
                        PremioGridItem(
                            premio = premio,
                            onClick = {
                                premioSelecionado = premio
                                exibirDialogo = true
                            }
                        )
                    }
                }
            } // fecha o bloco do else

            // Modal de Confirmação
            if (exibirDialogo && premioSelecionado != null) {
                GlassmorphismDialog(
                    titulo = "Confirmar Resgate",
                    mensagem = "Deseja trocar ${premioSelecionado!!.pontos} pontos por: ${premioSelecionado!!.titulo}?",
                    textoConfirmar = "Resgatar",
                    onDismissRequest = { exibirDialogo = false },
                    onConfirm = {
                        exibirDialogo = false
                        viewModel.resgatar(premioSelecionado!!) {
                            onTrocaConfirmada(premioSelecionado!!.pontos)
                        }
                    }
                )
            }
        } // fecha a Box (linha 94)
    } // fecha a Column (linha 63)
} // fecha a função CatalogoScreen (linha 54)

@Composable
fun PremioGridItem(
    premio: PromocaoResponse,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark.copy(alpha = 0.9f))
            .border(1.dp, PrimaryGold.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = premio.imageUrl,
            contentDescription = premio.titulo,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = premio.titulo,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = premio.descricao,
                color = TextSecondary,
                fontSize = 11.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.height(30.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${premio.pontos} pts",
                color = PrimaryGold,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}