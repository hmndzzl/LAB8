package com.example.lab8.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.lab8.data.local.entities.PhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {

    @Query("SELECT * FROM photos WHERE queryKey = :queryKey ORDER BY pageIndex, id")
    fun getPhotosByQuery(queryKey: String): PagingSource<Int, PhotoEntity>

    @Query("SELECT * FROM photos WHERE queryKey = :queryKey AND pageIndex = :page ORDER BY id")
    suspend fun getPhotosByQueryAndPage(queryKey: String, page: Int): List<PhotoEntity>

    @Query("SELECT * FROM photos WHERE id = :photoId LIMIT 1")
    suspend fun getPhotoById(photoId: Long): PhotoEntity?

    @Query("SELECT * FROM photos WHERE isFavorite = 1 ORDER BY updatedAt DESC")
    fun getFavoritePhotos(): Flow<List<PhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<PhotoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoEntity)

    @Query("UPDATE photos SET isFavorite = :isFavorite WHERE id = :photoId")
    suspend fun updateFavoriteStatus(photoId: Long, isFavorite: Boolean)

    @Query("DELETE FROM photos WHERE queryKey = :queryKey")
    suspend fun deletePhotosByQuery(queryKey: String)

    @Query("DELETE FROM photos WHERE queryKey = :queryKey AND pageIndex = :page")
    suspend fun deletePhotosByQueryAndPage(queryKey: String, page: Int)

    @Query("DELETE FROM photos WHERE updatedAt < :threshold AND isFavorite = 0")
    suspend fun deleteOldNonFavorites(threshold: Long)

    @Query("SELECT COUNT(*) FROM photos WHERE queryKey = :queryKey")
    suspend fun getPhotoCount(queryKey: String): Int
}