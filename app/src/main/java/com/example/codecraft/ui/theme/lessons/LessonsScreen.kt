package com.example.codecraft.ui.lessons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.codecraft.data.model.Lesson
import com.example.codecraft.data.model.PythonContent
import com.example.codecraft.ui.navigation.Screen

import com.example.codecraft.ui.theme.*

@Composable
fun LessonsScreen(
    navController: NavController,
    showBackButton: Boolean = true,
    onBack: () -> Unit = {}
) {
    val lessons = PythonContent.lessons

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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(lessons) { lesson ->
                LessonCard(lesson = lesson) {
                    navController.navigate(Screen.LessonDetail.createRoute(lesson.id))
                }
            }
        }
    }
}

@Composable
fun LessonCard(lesson: Lesson, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Accent.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📖",
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = lesson.title,
                    color = TextPrim,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = lesson.description,
                    color = TextSecond,
                    fontSize = 14.sp
                )
                Text(
                    text = "⭐ +${lesson.rewardPoints} очков",
                    color = Accent,
                    fontSize = 12.sp
                )
            }
        }
    }
}