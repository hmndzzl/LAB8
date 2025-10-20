package com.example.lab8.data.local.dao

import androidx.room.*
import com.example.lab8.data.local.entities.RecentQueryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentQueryDao {

    @Query("SELECT * FROM recent_queries ORDER BY lastUsedAt DESC LIMIT :limit")
    fun getRecentQueries(limit: Int = 10): Flow<List<RecentQueryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuery(query: RecentQueryEntity)

    @Query("UPDATE recent_queries SET lastUsedAt = :timestamp WHERE `query` = :query")
    suspend fun updateQueryTimestamp(query: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM recent_queries WHERE `query` = :query")
    suspend fun deleteQuery(query: String)

    @Query("DELETE FROM recent_queries WHERE `query` NOT IN (SELECT `query` FROM recent_queries ORDER BY lastUsedAt DESC LIMIT :limit)")
    suspend fun deleteOldQueries(limit: Int = 10)
}