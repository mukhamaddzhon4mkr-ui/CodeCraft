package com.example.codecraft.data.repository

import com.example.codecraft.data.db.dao.NotificationDao
import com.example.codecraft.data.db.dao.UserDao
import com.example.codecraft.data.db.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

class NotificationRepository(
    private val notificationDao: NotificationDao,
    private val userDao: UserDao
) {

    fun getNotifications(userId: Long): Flow<List<NotificationEntity>> {
        return notificationDao.getNotificationsByUser(userId)
    }

    suspend fun addNotification(userId: Long, title: String, message: String) {
        if (userDao.findById(userId) == null) return

        notificationDao.insert(
            NotificationEntity(
                userId = userId,
                title = title,
                message = message
            )
        )
    }

    suspend fun markAllAsRead(userId: Long) {
        notificationDao.markAllAsRead(userId)
    }

    suspend fun clearAll(userId: Long) {
        notificationDao.clearAll(userId)
    }
}
