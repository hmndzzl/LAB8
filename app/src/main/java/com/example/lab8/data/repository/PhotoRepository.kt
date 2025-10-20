package com.example.lab8.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.lab8.data.local.AppDatabase
import com.example.lab8.data.local.entities.PhotoEntity
import com.example.lab8.data.local.entities.RecentQueryEntity
import com.example.lab8.data.mappers.normalizeQuery
import com.example.lab8.data.paging.PhotoRemoteMediator
import com.example.lab8.Models.PexelsService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PhotoRepository(private val database: AppDatabase) {

    private val photoDao = database.photoDao()
    private val recentQueryDao = database.recentQueryDao()

    @OptIn(ExperimentalPagingApi::class)
    fun getPhotosPaged(query: String): Flow<PagingData<PhotoEntity>> {
        val normalizedQuery = query.normalizeQuery()

        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            remoteMediator = PhotoRemoteMediator(
                query = normalizedQuery,
                database = database
            ),
            pagingSourceFactory = { photoDao.getPhotosByQuery(normalizedQuery) }
        ).flow
    }

    fun getFavoritePhotos(): Flow<List<PhotoEntity>> {
        return photoDao.getFavoritePhotos()
    }

    suspend fun toggleFavorite(photoId: Long) {
        val photo = photoDao.getPhotoById(photoId)
        if (photo != null) {
            photoDao.updateFavoriteStatus(photoId, !photo.isFavorite)
        }
    }

    suspend fun getPhotoById(photoId: Long): PhotoEntity? {
        return photoDao.getPhotoById(photoId)
    }

    suspend fun saveRecentQuery(query: String) {
        val normalized = query.normalizeQuery()
        recentQueryDao.insertQuery(
            RecentQueryEntity(
                query = normalized,
                lastUsedAt = System.currentTimeMillis()
            )
        )
        recentQueryDao.deleteOldQueries(10)
    }

    fun getRecentQueries(): Flow<List<RecentQueryEntity>> {
        return recentQueryDao.getRecentQueries(10)
    }

    suspend fun deleteRecentQuery(query: String) {
        recentQueryDao.deleteQuery(query)
    }

    // Manual fetch for details screen (with network fallback)
    suspend fun fetchPhotoDetails(photoId: Long): PhotoEntity? {
        // Try local first
        val local = photoDao.getPhotoById(photoId)
        if (local != null) return local

        // If not in cache, could fetch from network (optional)
        // For now, just return null
        return null
    }
}