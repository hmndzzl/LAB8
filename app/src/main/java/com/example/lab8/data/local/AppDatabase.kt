package com.example.lab8.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.lab8.data.local.dao.PhotoDao
import com.example.lab8.data.local.dao.RecentQueryDao
import com.example.lab8.data.local.dao.RemoteKeyDao
import com.example.lab8.data.local.entities.PhotoEntity
import com.example.lab8.data.local.entities.RecentQueryEntity
import com.example.lab8.data.local.entities.RemoteKeyEntity

@Database(
    entities = [
        PhotoEntity::class,
        RecentQueryEntity::class,
        RemoteKeyEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun photoDao(): PhotoDao
    abstract fun recentQueryDao(): RecentQueryDao
    abstract fun remoteKeyDao(): RemoteKeyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pexels_gallery_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}