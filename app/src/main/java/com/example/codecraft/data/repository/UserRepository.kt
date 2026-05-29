package com.example.codecraft.data.repository

import com.example.codecraft.data.db.dao.UserDao
import com.example.codecraft.data.db.entity.UserEntity
import java.security.MessageDigest

class UserRepository(private val userDao: UserDao) {

    /** Регистрация нового пользователя */
    suspend fun register(
        username: String,
        email: String,
        password: String
    ): Result<UserEntity> {
        if (userDao.emailExists(email)) {
            return Result.failure(Exception("Этот email уже зарегистрирован"))
        }
        val user = UserEntity(
            username = username.trim(),
            email = email.trim().lowercase(),
            passwordHash = hashPassword(password)
        )
        val id = userDao.insert(user)
        return Result.success(user.copy(id = id))
    }

    /** Вход по email + пароль */
    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = userDao.login(
            email = email.trim().lowercase(),
            passwordHash = hashPassword(password)
        )
        return if (user != null) {
            Result.success(user)
        } else {
            Result.failure(Exception("Неверный email или пароль"))
        }
    }

    suspend fun findById(id: Long): UserEntity? = userDao.findById(id)

    /** SHA-256 хэш пароля */
    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
