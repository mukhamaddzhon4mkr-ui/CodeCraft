package com.example.codecraft.data.repository

import com.example.codecraft.data.db.dao.ProgressDao
import com.example.codecraft.data.db.entity.ProgressEntity
import kotlinx.coroutines.flow.Flow

class ProgressRepository(private val progressDao: ProgressDao) {


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
        }
    }
}