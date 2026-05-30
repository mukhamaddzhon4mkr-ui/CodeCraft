package com.example.codecraft.ui.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*


private val BgDark     = Color(0xFF0D1117)
private val Surface    = Color(0xFF161B22)
private val SurfaceAlt = Color(0xFF21262D)
private val Accent     = Color(0xFF00FF94)
private val AccentDim  = Color(0xFF00CC75)
private val TextPrim   = Color(0xFFE6EDF3)
private val TextSecond = Color(0xFF8B949E)
private val ErrorColor = Color(0xFFFF5555)
private val TabInactive= Color(0xFF30363D)

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onAuthSuccess: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    var isLoginMode by remember { mutableStateOf(true) }

    // Поля
    var username    by remember { mutableStateOf("") }
    var email       by remember { mutableStateOf("") }
    var password    by remember { mutableStateOf("") }
    var confirmPwd  by remember { mutableStateOf("") }
    var pwdVisible  by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) onAuthSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Accent.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        radius = 800f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))

            LogoSection()

            Spacer(Modifier.height(40.dp))

            TabSwitcher(
                isLoginMode = isLoginMode,
                onTabSelected = {
                    isLoginMode = it
                    viewModel.resetState()
                    username = ""; email = ""; password = ""; confirmPwd = ""
                }
            )

            Spacer(Modifier.height(32.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, SurfaceAlt)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AnimatedContent(
                        targetState = isLoginMode,
                        transitionSpec = {
                            slideInHorizontally { if (targetState) -it else it } togetherWith
                            slideOutHorizontally { if (targetState) it else -it }
                        },
                        label = "form"
                    ) { loginMode ->
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            if (!loginMode) {
                                CodeField(
                                    value = username,
                                    onValueChange = { username = it },
                                    label = "Имя пользователя",
                                    placeholder = "hero_coder",
                                    leadingIcon = "👤",
                                    imeAction = ImeAction.Next,
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                )
                            }

                            CodeField(
                                value = email,
                                onValueChange = { email = it },
                                label = "Email",
                                placeholder = "you@example.com",
                                leadingIcon = "📧",
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next,
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            )

                            CodeField(
                                value = password,
                                onValueChange = { password = it },
                                label = "Пароль",
                                placeholder = "••••••••",
                                leadingIcon = "🔑",
                                isPassword = true,
                                passwordVisible = pwdVisible,
                                onPasswordToggle = { pwdVisible = !pwdVisible },
                                imeAction = if (loginMode) ImeAction.Done else ImeAction.Next,
                                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                                onDone = {
                                    focusManager.clearFocus()
                                    if (loginMode) viewModel.login(email, password)
                                }
                            )

                            if (!loginMode) {
                                CodeField(
                                    value = confirmPwd,
                                    onValueChange = { confirmPwd = it },
                                    label = "Подтвердите пароль",
                                    placeholder = "••••••••",
                                    leadingIcon = "🔒",
                                    isPassword = true,
                                    passwordVisible = pwdVisible,
                                    onPasswordToggle = { pwdVisible = !pwdVisible },
                                    imeAction = ImeAction.Done,
                                    onDone = {
                                        focusManager.clearFocus()
                                        viewModel.register(username, email, password, confirmPwd)
                                    }
                                )
                            }
                        }
                    }

                    AnimatedVisibility(visible = authState is AuthState.Error) {
                        val msg = (authState as? AuthState.Error)?.message ?: ""
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(ErrorColor.copy(alpha = 0.12f))
                                .border(1.dp, ErrorColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("⚠️", fontSize = 16.sp)
                            Text(
                                text = msg,
                                color = ErrorColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    val isLoading = authState is AuthState.Loading
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            if (isLoginMode) viewModel.login(email, password)
                            else viewModel.register(username, email, password, confirmPwd)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Accent,
                            contentColor = BgDark,
                            disabledContainerColor = AccentDim.copy(alpha = 0.4f)
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = BgDark,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (isLoginMode) "Войти" else "Создать аккаунт",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isLoginMode) "Нет аккаунта? " else "Уже есть аккаунт? ",
                    color = TextSecond,
                    fontSize = 14.sp
                )
                Text(
                    text = if (isLoginMode) "Зарегистрироваться" else "Войти",
                    color = Accent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        isLoginMode = !isLoginMode
                        viewModel.resetState()
                    }
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}



@Composable
private fun LogoSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Accent.copy(alpha = 0.3f), Surface)
                    )
                )
                .border(2.dp, Accent.copy(alpha = 0.6f), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("</>", fontSize = 40.sp, color = Accent, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "CodeCraft",
            color = TextPrim,
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp
            )
        )
        Text(
            text = "АРХИТЕКТУРНАЯ СПЕЦИФИКАЦИЯ",
            color = Accent,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = "Мастерство кода начинается здесь",
            color = TextSecond,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}



@Composable
private fun TabSwitcher(isLoginMode: Boolean, onTabSelected: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceAlt)
            .padding(4.dp)
    ) {
        listOf(true to "Вход", false to "Регистрация").forEach { (mode, label) ->
            val selected = isLoginMode == mode
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (selected) Accent else Color.Transparent
                    )
                    .clickable { onTabSelected(mode) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = if (selected) BgDark else TextSecond,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 14.sp
                )
            }
        }
    }
}


@Composable
private fun CodeField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordToggle: (() -> Unit)? = null,
    onNext: (() -> Unit)? = null,
    onDone: (() -> Unit)? = null
) {
    val visual = if (isPassword && !passwordVisible)
        PasswordVisualTransformation() else VisualTransformation.None

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = label, color = TextSecond, fontSize = 12.sp, fontWeight = FontWeight.Medium)

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = TextSecond.copy(alpha = 0.5f)) },
            leadingIcon = { Text(leadingIcon, fontSize = 16.sp, modifier = Modifier.padding(start = 4.dp)) },
            trailingIcon = if (isPassword && onPasswordToggle != null) {
                {
                    IconButton(onClick = onPasswordToggle) {
                        Text(if (passwordVisible) "🙈" else "👁️", fontSize = 16.sp)
                    }
                }
            } else null,
            visualTransformation = visual,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isPassword) KeyboardType.Password else keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onNext = { onNext?.invoke() },
                onDone = { onDone?.invoke() }
            ),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Accent,
                unfocusedBorderColor = TabInactive,
                focusedTextColor = TextPrim,
                unfocusedTextColor = TextPrim,
                cursorColor = Accent,
                focusedContainerColor = SurfaceAlt,
                unfocusedContainerColor = SurfaceAlt,
                focusedLabelColor = Accent,
                unfocusedLabelColor = TextSecond,
            )
        )
    }
}
