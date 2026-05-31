package com.example.codecraft.ui.theme.lessons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.codecraft.data.SessionManager
import com.example.codecraft.data.db.AppDatabase
import com.example.codecraft.data.model.Lesson
import com.example.codecraft.data.model.PythonContent
import com.example.codecraft.data.repository.ProgressRepository
import com.example.codecraft.ui.theme.navigation.Screen

import com.example.codecraft.ui.theme.*

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.codecraft.ui.theme.lessons.viewmodel.LessonsUiState
import com.example.codecraft.ui.theme.lessons.viewmodel.LessonsViewModel
import com.example.codecraft.ui.viewmodel.ViewModelFactory

@Composable
fun LessonsScreen(
    navController: NavController,
    sessionManager: SessionManager,
    showBackButton: Boolean = true,
    onBack: () -> Unit = {},
    viewModelFactory: ViewModelFactory
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    
    val viewModel: LessonsViewModel = viewModel(factory = viewModelFactory)
    
    val uiState by viewModel.uiState.collectAsState()
    val userId = sessionManager.userId
    
    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.loadLessons(userId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .statusBarsPadding()
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBackButton) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = Accent,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Text(
                text = "📘 Уроки Python",
                color = TextPrim,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        when (val state = uiState) {
            is LessonsUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Accent)
                }
            }
            is LessonsUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message, color = Color.Red)
                }
            }
            is LessonsUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.lessons.indices.toList()) { index ->
                        val lesson = state.lessons[index]
                        val isCompleted = state.completedLessonIds.contains(lesson.id)
                        val isLocked = index > 0 && !state.completedLessonIds.contains(state.lessons[index - 1].id)
                        
                        LessonCard(
                            lesson = lesson, 
                            isCompleted = isCompleted,
                            isLocked = isLocked
                        ) {
                            if (!isLocked) {
                                navController.navigate(Screen.LessonDetail.createRoute(lesson.id))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LessonCard(
    lesson: Lesson, 
    isCompleted: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isLocked) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLocked) Surface.copy(alpha = 0.5f) else Surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp, 
            if (isLocked) Color.Gray.copy(alpha = 0.3f) else Accent.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isLocked) "🔒" else if (isCompleted) "✅" else "📖",
                fontSize = 32.sp,
                modifier = Modifier.alpha(if (isLocked) 0.5f else 1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    color = if (isLocked) TextSecond else TextPrim,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = lesson.description,
                    color = TextSecond,
                    fontSize = 14.sp
                )
                if (!isLocked) {
                    Text(
                        text = "⭐ +${lesson.rewardPoints} очков",
                        color = Accent,
                        fontSize = 12.sp
                    )
                }
            }
            if (isLocked) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Заблокировано",
                    tint = TextSecond,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
