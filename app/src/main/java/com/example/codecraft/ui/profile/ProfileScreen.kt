package com.example.codecraft.ui.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codecraft.data.SessionManager
import com.example.codecraft.data.db.AppDatabase
import com.example.codecraft.data.db.entity.UserEntity
import com.example.codecraft.data.repository.ProgressRepository
import com.example.codecraft.data.repository.UserRepository
import kotlinx.coroutines.flow.collectLatest

private val BgDark = Color(0xFF0D1117)
private val Surface = Color(0xFF161B22)
private val Accent = Color(0xFF00FF94)
private val TextPrim = Color(0xFFE6EDF3)
private val TextSecond = Color(0xFF8B949E)
private val CardBorder = Color(0xFF30363D)

@Composable
fun ProfileScreen(
    sessionManager: SessionManager,
    onLogout: () -> Unit,
    onEditProfile: () -> Unit,
    onSettings: () -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    
    val db = remember { AppDatabase.getInstance(context) }
    val userRepository = remember { UserRepository(db.userDao()) }
    val progressRepository = remember { ProgressRepository(db.progressDao()) }
    val userId = sessionManager.userId

    var user by remember { mutableStateOf<UserEntity?>(null) }
    var totalScore by remember { mutableIntStateOf(0) }
    var completedLessons by remember { mutableIntStateOf(0) }

    LaunchedEffect(userId) {
        if (userId != null) {
            user = userRepository.findById(userId)
            progressRepository.getTotalScore(userId).collect { totalScore = it }
        }
    }
    
    LaunchedEffect(userId) {
        if (userId != null) {
            db.progressDao().completedLessonsCount(userId).collect { completedLessons = it }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BgDark,
        modifier = Modifier.statusBarsPadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Аватар
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Accent, Color(0xFF00B268)))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user?.avatarEmoji ?: user?.username?.take(1)?.uppercase() ?: "👤",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = user?.username ?: "Загрузка...",
                color = TextPrim,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = user?.email ?: "",
                color = TextSecond,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Статистика
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Star,
                    label = "Всего XP",
                    value = totalScore.toString(),
                    color = Color(0xFFFFD700)
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.CheckCircle,
                    label = "Уроков",
                    value = completedLessons.toString(),
                    color = Accent
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Настройки/Действия
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Surface)
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            ) {
                ProfileMenuItem(
                    icon = Icons.Default.Person,
                    title = "Редактировать профиль",
                    onClick = onEditProfile
                )
                HorizontalDivider(color = CardBorder, thickness = 1.dp)
                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    title = "Настройки",
                    onClick = onSettings
                )
                HorizontalDivider(color = CardBorder, thickness = 1.dp)
                ProfileMenuItem(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    title = "Выйти",
                    textColor = Color(0xFFFF5555),
                    onClick = onLogout
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun StatCard(modifier: Modifier, icon: ImageVector, label: String, value: String, color: Color) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, color = TextPrim, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = label, color = TextSecond, fontSize = 12.sp)
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    textColor: Color = TextPrim,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = if (textColor == TextPrim) Accent else textColor, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextSecond)
    }
}
