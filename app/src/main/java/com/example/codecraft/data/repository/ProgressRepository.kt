package com.example.codecraft.data.repository

import com.example.codecraft.data.db.dao.NotificationDao
import com.example.codecraft.data.db.dao.ProgressDao
import com.example.codecraft.data.db.dao.UserDao
import com.example.codecraft.data.db.entity.NotificationEntity
import com.example.codecraft.data.db.entity.ProgressEntity
import kotlinx.coroutines.flow.Flow

class ProgressRepository(
    private val progressDao: ProgressDao,
    private val notificationDao: NotificationDao,
    private val userDao: UserDao
) {


    fun getProgressByLanguage(userId: Long, language: String): Flow<List<ProgressEntity>> {
        return progressDao.getByLanguage(userId, language)
    }

    fun getTotalScore(userId: Long): Flow<Int> {
        return progressDao.totalScore(userId)
    }

    suspend fun completeLesson(
        userId: Long,
        language: String,
        lessonId: String,
        lessonTitle: String,
        score: Int
    ) {
        // Verify user exists to avoid FK constraint violations
        if (userDao.findById(userId) == null) return

        val existingProgress = progressDao.getLesson(userId, lessonId)

        if (existingProgress == null) {
            val progress = ProgressEntity(
                userId = userId,
                language = language,
                lessonId = lessonId,
                lessonTitle = lessonTitle,
                isCompleted = true,
                score = score,
                completedAt = System.currentTimeMillis(),
                attempts = 1
            )
            progressDao.insertOrUpdate(progress)

            // Add notification
            notificationDao.insert(
                NotificationEntity(
                    userId = userId,
                    title = "Урок пройден!",
                    message = "Вы успешно завершили урок: $lessonTitle и получили $score очков."
                )
            )
        }
    }
}
