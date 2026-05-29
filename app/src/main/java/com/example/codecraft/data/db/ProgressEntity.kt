package com.example.codecraft.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Таблица прогресса обучения
 *
 * Каждая запись = один урок, пройденный конкретным пользователем
 */
@Entity(
    tableName = "progress",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE   // При удалении юзера — удаляем и прогресс
        )
    ],
    indices = [Index("userId")]
)
data class ProgressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val language: String,           // "kotlin", "python", "javascript" и т.д.
    val lessonId: String,           // Уникальный ID урока, например "kotlin_basics_1"
    val lessonTitle: String,
    val isCompleted: Boolean = false,
    val score: Int = 0,             // Очки за урок (0–100)
    val completedAt: Long? = null,  // null если ещё не завершён
    val attempts: Int = 0
)
