package com.example.codecraft.ui.theme.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.codecraft.data.SessionManager
import com.example.codecraft.data.db.AppDatabase
import com.example.codecraft.data.repository.NotificationRepository
import com.example.codecraft.data.repository.ProgressRepository
import com.example.codecraft.data.repository.UserRepository
import kotlinx.coroutines.launch
import com.example.codecraft.ui.theme.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.codecraft.ui.theme.home.viewmodel.HomeUiState
import com.example.codecraft.ui.theme.home.viewmodel.HomeViewModel
import com.example.codecraft.ui.viewmodel.ViewModelFactory

@Composable
fun HomeScreen(
    navController: NavController,
    sessionManager: SessionManager,
    onLogout: () -> Unit,
    viewModelFactory: ViewModelFactory
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    
    val viewModel: HomeViewModel = viewModel(factory = viewModelFactory)

    val uiState by viewModel.uiState.collectAsState()
    val userId = sessionManager.userId
    val scope = rememberCoroutineScope()
    
    val notificationRepository = remember { NotificationRepository(db.notificationDao(), db.userDao()) }
    val notifications by if (userId != null) {
        notificationRepository.getNotifications(userId).collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList()) }
    }

    var showNotifications by remember { mutableStateOf(false) }
    
    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.loadDashboardData(userId)
        } else {
            onLogout()
        }
    }

    // Обработка выхода если пользователь удален
    LaunchedEffect(uiState) {
        if (uiState is HomeUiState.Error && (uiState as HomeUiState.Error).message.contains("не найден")) {
            onLogout()
        }
    }

    if (showNotifications) {
        LaunchedEffect(Unit) {
            if (userId != null) {
                notificationRepository.markAllAsRead(userId)
            }
        }
        NotificationsBottomSheet(
            notifications = notifications,
            onDismiss = { showNotifications = false },
            onClearAll = {
                if (userId != null) {
                    scope.launch {
                        notificationRepository.clearAll(userId)
                    }
                }
            }
        )
    }

    when (val state = uiState) {
        is HomeUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize().background(BgDark), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Accent)
            }
        }
        is HomeUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize().background(BgDark).padding(16.dp), contentAlignment = Alignment.Center) {
                Text(state.message, color = Color.Red, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }
        is HomeUiState.Success -> {
            HomeScreenContent(
                navController = navController,
                username = state.username,
                totalScore = state.totalScore,
                news = state.news,
                notifications = notifications,
                onShowNotifications = { showNotifications = true }
            )
        }
    }
}

@Composable
fun HomeScreenContent(
    navController: NavController,
    username: String,
    totalScore: Int,
    news: List<com.example.codecraft.data.api.NewsItem>,
    notifications: List<com.example.codecraft.data.db.entity.NotificationEntity>,
    onShowNotifications: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Surface)
                        .border(1.dp, Accent.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("</>", color = Accent, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Привет, $username!", color = Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("CodeCraft", color = TextPrim, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            IconButton(
                onClick = onShowNotifications,
                modifier = Modifier.background(Surface, CircleShape)
            ) {
                BadgedBox(
                    badge = {
                        if (notifications.any { !it.isRead }) {
                            Badge(containerColor = Accent)
                        }
                    }
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Accent)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF00FF94), Color(0xFF00B268))
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Text("Твой уровень: ${totalScore / 500 + 1}", color = BgDark, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$totalScore XP",
                    color = BgDark,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.weight(1f))
                LinearProgressIndicator(
                    progress = { (totalScore % 500) / 500f },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                    color = BgDark,
                    trackColor = BgDark.copy(alpha = 0.2f),
                )
            }
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.align(Alignment.TopEnd).size(48.dp),
                tint = BgDark.copy(alpha = 0.2f)
            )
        }

        if (news.isNotEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                "Новости IT (из API)",
                color = TextPrim,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            news.forEach { item ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(item.title, color = Accent, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                        Text(item.body, color = TextSecond, fontSize = 12.sp, maxLines = 2)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            "Выбери язык обучения",
            color = TextPrim,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val languages = listOf(
            LanguageItem("Python", "🐍", "Самый популярный", Color(0xFF3776AB), true),
            LanguageItem("Kotlin", "🎯", "Для Android", Color(0xFF7F52FF), false),
            LanguageItem("Java", "☕", "Классика Enterprise", Color(0xFFED8B00), false),
            LanguageItem("Go", "🐹", "Высокая скорость", Color(0xFF00ADD8), false),
            LanguageItem("C++", "⚡", "Максимальный контроль", Color(0xFF00599C), false)
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(languages) { lang ->
                LanguageCard(lang) {
                    if (lang.enabled) navController.navigate("lessons")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            "Твоя активность",
            color = TextPrim,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("🔥", fontSize = 24.sp)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Ударный режим", color = TextPrim, fontWeight = FontWeight.Bold)
                    Text("Ты учишься 3 дня подряд!", color = TextSecond, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

data class LanguageItem(val name: String, val emoji: String, val desc: String, val color: Color, val enabled: Boolean)

@Composable
fun LanguageCard(lang: LanguageItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(160.dp, 180.dp)
            .clickable(enabled = lang.enabled) { onClick() },
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(24.dp),
        border = if (lang.enabled) BorderStroke(1.dp, Accent.copy(alpha = 0.3f)) else null
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.size(48.dp).background(lang.color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(lang.emoji, fontSize = 24.sp)
            }
            Column {
                Text(lang.name, color = TextPrim, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(lang.desc, color = TextSecond, fontSize = 11.sp)
            }
            if (!lang.enabled) {
                Text("Скоро", color = Accent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
