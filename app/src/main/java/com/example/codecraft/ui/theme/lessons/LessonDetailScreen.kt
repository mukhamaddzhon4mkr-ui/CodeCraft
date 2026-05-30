package com.example.codecraft.ui.lessons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.navigation.NavController
import com.example.codecraft.data.SessionManager
import com.example.codecraft.data.db.AppDatabase
import com.example.codecraft.data.model.Lesson
import com.example.codecraft.data.model.PythonContent
import com.example.codecraft.data.repository.ProgressRepository
import kotlinx.coroutines.launch

private val BgDark = Color(0xFF0D1117)
private val Surface = Color(0xFF161B22)
private val Accent = Color(0xFF00FF94)
private val TextPrim = Color(0xFFE6EDF3)
private val TextSecond = Color(0xFF8B949E)
private val ErrorColor = Color(0xFFFF5555)

@Composable
fun LessonDetailScreen(
    navController: NavController,
    sessionManager: SessionManager,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val progressRepository = remember { ProgressRepository(db.progressDao()) }
    val scope = rememberCoroutineScope()

    val lessonId = navController.previousBackStackEntry?.savedStateHandle?.get<String>("lessonId")

    val lesson = remember(lessonId) {
        PythonContent.lessons.find { it.id == lessonId }
    }

    // Находим индекс текущего урока и следующий урок
    val currentIndex = lesson?.let { PythonContent.lessons.indexOf(it) } ?: -1
    val nextLesson = if (currentIndex != -1 && currentIndex + 1 < PythonContent.lessons.size) {
        PythonContent.lessons[currentIndex + 1]
    } else {
        null
    }

    var selectedAnswer by remember { mutableStateOf("") }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    var isAnswered by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    if (lesson == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDark),
            contentAlignment = Alignment.Center
        ) {
            Text("Урок не найден", color = ErrorColor)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ← Кнопка "Назад" (теперь работает)
            IconButton(onClick = onBack) {
                Text("←", color = Accent, fontSize = 24.sp)
            }
            Text(
                text = lesson.title,
                color = TextPrim,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            // Индикатор прогресса
            Text(
                text = "${currentIndex + 1}/${PythonContent.lessons.size}",
                color = TextSecond,
                fontSize = 14.sp
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📖 Теория",
                    color = Accent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = lesson.theoryText,
                    color = TextSecond,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }

        // Вопрос
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "❓ Вопрос",
                    color = Accent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = lesson.question,
                    color = TextPrim,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Варианты ответов
                lesson.options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (selectedAnswer == option) Accent.copy(alpha = 0.2f)
                                else Color.Transparent
                            )
                            .clickable(enabled = !isAnswered) { selectedAnswer = option }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedAnswer == option,
                            onClick = { if (!isAnswered) selectedAnswer = option },
                            colors = RadioButtonDefaults.colors(selectedColor = Accent)
                        )
                        Text(
                            text = option,
                            color = if (selectedAnswer == option) Accent else TextSecond,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!isAnswered) {
                    Button(
                        onClick = {
                            isAnswered = true
                            if (selectedAnswer == lesson.correctAnswer) {
                                isCorrect = true
                                resultMessage = "✅ Правильно! +${lesson.rewardPoints} очков"
                                val userId = sessionManager.userId
                                if (userId != null) {
                                    scope.launch {
                                        progressRepository.completeLesson(
                                            userId = userId,
                                            language = lesson.language,
                                            lessonId = lesson.id,
                                            lessonTitle = lesson.title,
                                            score = lesson.rewardPoints
                                        )
                                    }
                                }
                            } else {
                                isCorrect = false
                                resultMessage = "❌ Неправильно. Правильный ответ: ${lesson.correctAnswer}"
                            }
                        },
                        enabled = selectedAnswer.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Accent)
                    ) {
                        Text("Проверить ответ", color = BgDark, fontWeight = FontWeight.Bold)
                    }
                }

                if (resultMessage != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCorrect) Accent.copy(alpha = 0.1f) else ErrorColor.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = resultMessage!!,
                                color = if (isCorrect) Accent else ErrorColor,
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            if (nextLesson != null) {
                                Button(
                                    onClick = {
                                        selectedAnswer = ""
                                        resultMessage = null
                                        isAnswered = false
                                        isCorrect = false
                                        navController.currentBackStackEntry?.savedStateHandle?.set("lessonId", nextLesson.id)
                                        navController.navigate("lesson")
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Accent)
                                ) {
                                    Text("➡ Следующий урок: ${nextLesson.title}", color = BgDark, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = {
                                        onBack()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Accent)
                                ) {
                                    Text("🏆 Завершить обучение", color = BgDark, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}