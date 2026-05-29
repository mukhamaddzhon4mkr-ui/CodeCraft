package com.example.codecraft.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.codecraft.data.SessionManager

private val BgDark  = Color(0xFF0D1117)
private val Accent  = Color(0xFF00FF94)
private val TextPrim = Color(0xFFE6EDF3)
private val TextSecond = Color(0xFF8B949E)

@Composable
fun HomeScreen(
    sessionManager: SessionManager,
    onLogout: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            Text("🎉", fontSize = 64.sp)
            Text(
                text = "Добро пожаловать!",
                color = TextPrim,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "ID пользователя: ${sessionManager.userId}",
                color = TextSecond,
                fontSize = 14.sp
            )
            Text(
                text = "Главный экран обучения\nбудет здесь 🚀",
                color = TextSecond,
                fontSize = 16.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = onLogout,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Accent),
                border = androidx.compose.foundation.BorderStroke(1.dp, Accent.copy(alpha = 0.5f))
            ) {
                Text("Выйти из аккаунта")
            }
        }
    }
}
