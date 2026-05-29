package com.example.codecraft.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Таблица пользователей
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val email: String,
    val passwordHash: String,       // Храним хэш, не сырой пароль
    val createdAt: Long = System.currentTimeMillis(),
    val avatarEmoji: String = "🧑‍💻" // Аватар-эмодзи по умолчанию
)
