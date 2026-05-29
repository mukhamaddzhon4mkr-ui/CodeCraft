package com.example.codecraft.data.db.dao

import androidx.room.*
import com.example.codecraft.data.db.entity.ProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: ProgressEntity)

    /** Весь прогресс пользователя — реактивный Flow */
    @Query("SELECT * FROM progress WHERE userId = :userId ORDER BY completedAt DESC")
    fun getAllByUser(userId: Long): Flow<List<ProgressEntity>>

    /** Прогресс по конкретному языку */
    @Query("SELECT * FROM progress WHERE userId = :userId AND language = :language")
    fun getByLanguage(userId: Long, language: String): Flow<List<ProgressEntity>>

    /** Количество завершённых уроков */
    @Query("SELECT COUNT(*) FROM progress WHERE userId = :userId AND isCompleted = 1")
    fun completedLessonsCount(userId: Long): Flow<Int>

    /** Суммарный счёт пользователя */
    @Query("SELECT COALESCE(SUM(score), 0) FROM progress WHERE userId = :userId AND isCompleted = 1")
    fun totalScore(userId: Long): Flow<Int>

    /** Получить конкретный урок */
    @Query("SELECT * FROM progress WHERE userId = :userId AND lessonId = :lessonId LIMIT 1")
    suspend fun getLesson(userId: Long, lessonId: String): ProgressEntity?

    @Delete
    suspend fun delete(progress: ProgressEntity)
}
