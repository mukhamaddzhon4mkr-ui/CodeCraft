package com.example.codecraft.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import com.example.codecraft.data.db.dao.NotificationDao
import com.example.codecraft.data.db.dao.ProgressDao
import com.example.codecraft.data.db.dao.UserDao
import com.example.codecraft.data.db.entity.NotificationEntity
import com.example.codecraft.data.db.entity.ProgressEntity
import com.example.codecraft.data.db.entity.UserEntity

@Database(
    entities = [UserEntity::class, ProgressEntity::class, NotificationEntity::class],
    version = 4,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun progressDao(): ProgressDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "codecraft.db"
                )
                    .fallbackToDestructiveMigration()
                    .fallbackToDestructiveMigration()
                .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
