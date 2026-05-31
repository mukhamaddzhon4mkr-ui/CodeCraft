package com.example.codecraft.ui.theme.lessons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.codecraft.data.SessionManager
import com.example.codecraft.data.model.Lesson
import com.example.codecraft.data.model.PythonContent
import com.example.codecraft.ui.theme.*
import com.example.codecraft.ui.theme.lessons.viewmodel.LessonDetailUiState
import com.example.codecraft.ui.theme.lessons.viewmodel.LessonDetailViewModel
import com.example.codecraft.ui.theme.navigation.Screen
import com.example.codecraft.ui.viewmodel.ViewModelFactory



@Composable
fun LessonDetailScreen(
    lessonId: String?,
    navController: NavController,
    sessionManager: SessionManager,
    onBack: () -> Unit,
    viewModelFactory: ViewModelFactory
) {
    val viewModel: LessonDetailViewModel = viewModel(factory = viewModelFactory)
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(lessonId) {
        viewModel.loadLesson(lessonId)
    }

    when (val state = uiState) {
        is LessonDetailUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize().background(BgDark), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Accent)
            }
        }
        is LessonDetailUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize().background(BgDark), contentAlignment = Alignment.Center) {
                Text(state.message, color = ErrorColor)
            }
        }
        is LessonDetailUiState.Success -> {
            LessonDetailContent(
                lesson = state.lesson,
                nextLesson = state.nextLesson,
                currentIndex = state.currentIndex,
                totalLessons = PythonContent.lessons.size,
                onBack = onBack,
                onComplete = { viewModel.completeLesson(sessionManager.userId ?: -1, state.lesson) },
                navController = navController
            )
        }
    }
}

@Composable
fun LessonDetailContent(
    lesson: Lesson,
    nextLesson: Lesson?,
    currentIndex: Int,
    totalLessons: Int,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    navController: NavController
) {
    var selectedAnswer by remember { mutableStateOf("") }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    var isAnswered by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(60.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = Accent,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = lesson.title,
                    color = TextPrim,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${currentIndex + 1}/$totalLessons",
                    color = TextSecond,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
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
                                    onComplete()
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

                                if (isCorrect && nextLesson != null) {
                                    Button(
                                        onClick = {
                                            navController.navigate(Screen.LessonDetail.createRoute(nextLesson.id)) {
                                                popUpTo(Screen.LessonDetail.route) { inclusive = true }
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = Accent)
                                    ) {
                                        Text("➡ Следующий урок: ${nextLesson.title}", color = BgDark, fontWeight = FontWeight.Bold)
                                    }
                                } else if (isCorrect) {
                                    Button(
                                        onClick = { onBack() },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = Accent)
                                    ) {
                                        Text("🏆 Завершить обучение", color = BgDark, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            isAnswered = false
                                            resultMessage = null
                                            selectedAnswer = ""
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = ErrorColor)
                                    ) {
                                        Text("🔄 Попробовать еще раз", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
