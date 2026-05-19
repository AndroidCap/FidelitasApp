package com.g1.fidelitasapp.ui.extrato

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.g1.fidelitasapp.data.database.TransactionEntity
import com.g1.fidelitasapp.ui.theme.AccentGold
import com.g1.fidelitasapp.ui.theme.BackgroundDark
import com.g1.fidelitasapp.ui.theme.PrimaryGold
import com.g1.fidelitasapp.ui.theme.SurfaceDark
import com.g1.fidelitasapp.ui.theme.TextPrimary
import com.g1.fidelitasapp.ui.theme.TextSecondary

@Composable
fun ExtratoScreen(
    viewModel: ExtratoViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // 1. Header de Navegação
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = PrimaryGold
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "EXTRATO DE OPERAÇÕES",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = { viewModel.refreshExtrato() }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Recarregar",
                    tint = PrimaryGold
                )
            }
        }

        // 2. Conteúdo da Lista ou Estados (Erro / Carregamento)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            if (uiState.isLoading && uiState.transacoes.isEmpty()) {
                CircularProgressIndicator(
                    color = PrimaryGold,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (uiState.errorMessage != null && uiState.transacoes.isEmpty()) {
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
                        fontSize = 15.sp
                    )
                }
            } else if (uiState.transacoes.isEmpty()) {
                Text(
                    text = "Nenhuma transação encontrada.",
                    color = TextSecondary,
                    fontSize = 15.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.transacoes) { transaction ->
                        TransacaoItem(transaction)
                    }
                }
            }
        }
    }
}

@Composable
fun TransacaoItem(transaction: TransactionEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.dp, PrimaryGold.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Círculo dourado de indicação visual
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BackgroundDark),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (transaction.isEntrada) "+" else "-",
                    color = if (transaction.isEntrada) Color(0xFF00C853) else Color(0xFFCF6679), // Verde ou Vermelho suave
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = transaction.descricao,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = transaction.dataOperacao,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        // Quantidade de pontos
        Text(
            text = "${if (transaction.isEntrada) "+" else "-"}${transaction.pontos} pts",
            color = if (transaction.isEntrada) PrimaryGold else AccentGold,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
