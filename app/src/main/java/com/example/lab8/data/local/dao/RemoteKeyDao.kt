package com.example.lab8.data.local.dao

import androidx.room.*
import com.example.lab8.data.local.entities.RemoteKeyEntity

@Dao
interface RemoteKeyDao {

    @Query("SELECT * FROM remote_keys WHERE photoId = :photoId")
    suspend fun getRemoteKey(photoId: Long): RemoteKeyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeys(keys: List<RemoteKeyEntity>)

    @Query("DELETE FROM remote_keys WHERE queryKey = :queryKey")
    suspend fun deleteKeysByQuery(queryKey: String)

    @Query("DELETE FROM remote_keys")
    suspend fun clearAll()
}