package com.example.codecraft.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController  // ← ДОБАВИТЬ
import com.example.codecraft.data.SessionManager
import com.example.codecraft.data.db.AppDatabase
import com.example.codecraft.data.repository.ProgressRepository

private val BgDark  = Color(0xFF0D1117)
private val Accent  = Color(0xFF00FF94)
private val TextPrim = Color(0xFFE6EDF3)
private val TextSecond = Color(0xFF8B949E)

@Composable
fun HomeScreen(
    navController: NavController,
    sessionManager: SessionManager,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val progressRepository = remember { ProgressRepository(db.progressDao()) }

    val userId = sessionManager.userId

    var totalScore by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        if (userId != null) {
            progressRepository.getTotalScore(userId).collect { score ->
                totalScore = score
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

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
                text = "ID пользователя: ${userId ?: "не определен"}",
                color = TextSecond,
                fontSize = 14.sp
            )

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Accent,
                    strokeWidth = 2.dp
                )
            } else {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Accent.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "⭐ Всего очков",
                            color = TextSecond,
                            fontSize = 12.sp
                        )
                        Text(
                            text = totalScore.toString(),
                            color = Accent,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

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
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Accent
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Accent.copy(alpha = 0.5f))
            ) {
                Text("Выйти из аккаунта")
            }

            // ← Теперь navController виден
            Button(
                onClick = { navController.navigate("lessons") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Accent)
            ) {
                Text("Начать обучение Python", color = BgDark)
            }
        }
    }
}