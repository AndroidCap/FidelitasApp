package com.g1.fidelitasapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.g1.fidelitasapp.data.storage.SessionManager
import com.g1.fidelitasapp.ui.theme.BackgroundDark
import com.g1.fidelitasapp.ui.theme.PrimaryGold
import com.g1.fidelitasapp.ui.theme.TextPrimary
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    sessionManager: SessionManager,
    onLogout: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "BEM-VINDO À HOME!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryGold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Você foi autenticado com sucesso e sua sessão está ativa no DataStore.",
            color = TextPrimary,
            fontSize = 16.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botão Sair (Logout)
        Button(
            onClick = {
                coroutineScope.launch {
                    sessionManager.clearSession()
                    onLogout()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFCF6679), // Vermelho suave de saída
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("SAIR DO APLICATIVO", fontWeight = FontWeight.Bold)
        }
    }
}
