package com.example.codecraft.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codecraft.data.SessionManager
import com.example.codecraft.data.db.AppDatabase
import com.example.codecraft.data.db.entity.UserEntity
import com.example.codecraft.data.repository.UserRepository
import kotlinx.coroutines.launch

private val BgDark = Color(0xFF0D1117)
private val Surface = Color(0xFF161B22)
private val Accent = Color(0xFF00FF94)
private val TextPrim = Color(0xFFE6EDF3)
private val TextSecond = Color(0xFF8B949E)
private val CardBorder = Color(0xFF30363D)

@Composable
fun EditProfileScreen(
    sessionManager: SessionManager,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { AppDatabase.getInstance(context) }
    val userRepository = remember { UserRepository(db.userDao()) }
    val userId = sessionManager.userId

    var user by remember { mutableStateOf<UserEntity?>(null) }
    var username by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("🧑‍💻") }

    LaunchedEffect(userId) {
        if (userId != null) {
            user = userRepository.findById(userId)
            user?.let {
                username = it.username
                selectedEmoji = it.avatarEmoji
            }
        }
    }

    val emojis = listOf("🧑‍💻", "🚀", "🔥", "💻", "💡", "⚡", "🤖", "🎮", "🌟", "👾", "🦊", "🐼")

    Scaffold(
        containerColor = BgDark,
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            Column {
                Spacer(modifier = Modifier.height(60.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = Accent)
                    }
                    Text(
                        text = "Редактировать профиль",
                        color = TextPrim,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Surface)
                    .border(2.dp, Accent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = selectedEmoji, fontSize = 48.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Поле имени
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Имя пользователя", color = TextSecond) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrim,
                    unfocusedTextColor = TextPrim,
                    focusedBorderColor = Accent,
                    unfocusedBorderColor = CardBorder,
                    cursorColor = Accent
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Выберите аватар",
                color = TextPrim,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(12.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(emojis) { emoji ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedEmoji == emoji) Accent.copy(alpha = 0.2f) else Surface)
                            .border(
                                1.dp,
                                if (selectedEmoji == emoji) Accent else CardBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedEmoji = emoji },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 24.sp)
                        if (selectedEmoji == emoji) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Accent,
                                modifier = Modifier
                                    .size(16.dp)
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        user?.let {
                            userRepository.update(it.copy(username = username, avatarEmoji = selectedEmoji))
                            onBack()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Сохранить изменения", color = BgDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
