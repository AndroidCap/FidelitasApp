package com.g1.fidelitasapp.ui.extrato

import android.widget.Toast
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.g1.fidelitasapp.data.database.TransactionEntity
import com.g1.fidelitasapp.ui.theme.AccentGold
import com.g1.fidelitasapp.ui.theme.BackgroundDark
import com.g1.fidelitasapp.ui.theme.PrimaryGold
import com.g1.fidelitasapp.ui.theme.SurfaceDark
import com.g1.fidelitasapp.ui.theme.SurfaceDarkElevated
import com.g1.fidelitasapp.ui.theme.TextPrimary
import com.g1.fidelitasapp.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun ExtratoScreen(
    viewModel: ExtratoViewModel,
    saldoAtual: Int,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Observador de Toasts (Sucesso e Erro)
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
    ) {
        // 1. Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = TextPrimary
                )
            }
            Text(
                text = "Extrato de Pontos",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
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

        // 2. Card de Saldo
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceDark)
                .border(1.dp, PrimaryGold.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Saldo Atual",
                color = TextSecondary,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = String.format(Locale("pt", "BR"), "%,d", saldoAtual).replace(",", "."),
                color = AccentGold,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Lista
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (uiState.isLoading && uiState.transacoes.isEmpty()) {
                CircularProgressIndicator(
                    color = PrimaryGold,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 80.dp),
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
    val dataPartes = transaction.dataOperacao.split(" ")
    val dia = dataPartes.getOrNull(0) ?: "00"
    val mes = dataPartes.getOrNull(1) ?: "Mai"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark.copy(alpha = 0.9f))
            .border(
                width = 1.dp,
                color = PrimaryGold.copy(alpha = 0.15f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(44.dp)
            ) {
                Text(
                    text = dia,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = mes,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(36.dp)
                    .background(SurfaceDarkElevated)
            )
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.descricao,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        val sinal = if (transaction.isEntrada) "+" else "-"
        val valorCor = if (transaction.isEntrada) PrimaryGold else Color(0xFFCF6679)

        Text(
            text = "$sinal${transaction.pontos} pts",
            color = valorCor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
