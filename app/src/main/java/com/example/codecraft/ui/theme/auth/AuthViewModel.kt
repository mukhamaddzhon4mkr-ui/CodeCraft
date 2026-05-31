package com.example.codecraft.ui.theme.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codecraft.data.SessionManager
import com.example.codecraft.data.db.entity.UserEntity
import com.example.codecraft.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: UserEntity) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun register(username: String, email: String, password: String, confirmPassword: String) {
        if (!validateRegister(username, email, password, confirmPassword)) return

        viewModelScope.launch {
            _authState.value = AuthState.Loading

            try {
                withTimeout(5000) {
                    val result = userRepository.register(username, email, password)
                    result.fold(
                        onSuccess = { user ->
                            sessionManager.userId = user.id
                            _authState.value = AuthState.Success(user)
                        },
                        onFailure = { e ->
                            _authState.value = AuthState.Error(e.message ?: "Ошибка регистрации")
                        }
                    )
                }
            } catch (e: TimeoutCancellationException) {
                _authState.value = AuthState.Error("Превышено время ожидания. Проверьте соединение.")
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Произошла ошибка: ${e.message}")
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Заполните все поля")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading

            try {
                withTimeout(5000) {
                    val result = userRepository.login(email, password)
                    result.fold(
                        onSuccess = { user ->
                            sessionManager.userId = user.id
                            _authState.value = AuthState.Success(user)
                        },
                        onFailure = { e ->
                            _authState.value = AuthState.Error(e.message ?: "Ошибка входа")
                        }
                    )
                }
            } catch (e: TimeoutCancellationException) {
                _authState.value = AuthState.Error("Превышено время ожидания. Проверьте соединение.")
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Произошла ошибка: ${e.message}")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    private fun validateRegister(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        return when {
            username.isBlank() || email.isBlank() || password.isBlank() -> {
                _authState.value = AuthState.Error("Заполните все поля")
                false
            }
            username.length < 3 -> {
                _authState.value = AuthState.Error("Имя пользователя должно быть не менее 3 символов")
                false
            }
            username.length > 20 -> {
                _authState.value = AuthState.Error("Имя пользователя не должно превышать 20 символов")
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _authState.value = AuthState.Error("Введите корректный email адрес")
                false
            }
            password.length < 6 -> {
                _authState.value = AuthState.Error("Пароль должен быть не менее 6 символов")
                false
            }
            password.length > 50 -> {
                _authState.value = AuthState.Error("Пароль не должен превышать 50 символов")
                false
            }
            password != confirmPassword -> {
                _authState.value = AuthState.Error("Пароли не совпадают")
                false
            }
            else -> true
        }
    }
}