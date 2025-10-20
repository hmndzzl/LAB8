package com.example.lab8.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.lab8.data.local.AppDatabase
import com.example.lab8.data.local.entities.PhotoEntity
import com.example.lab8.data.local.entities.RemoteKeyEntity
import com.example.lab8.data.mappers.normalizeQuery
import com.example.lab8.data.mappers.toEntity
import com.example.lab8.Models.PexelsService

@OptIn(ExperimentalPagingApi::class)
class PhotoRemoteMediator(
    private val query: String,
    private val database: AppDatabase,
    private val api: PexelsService = PexelsService
) : RemoteMediator<Int, PhotoEntity>() {

    private val photoDao = database.photoDao()
    private val remoteKeyDao = database.remoteKeyDao()
    private val queryKey = query.normalizeQuery()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PhotoEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)

                    val remoteKey = remoteKeyDao.getRemoteKey(lastItem.id)
                    remoteKey?.nextPage ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val response = if (queryKey == "curated") {
                api.api.getCurated(page = page, perPage = state.config.pageSize)
            } else {
                api.api.searchPhotos(query = queryKey, page = page, perPage = state.config.pageSize)
            }

            val photos = response.photos
            val endOfPaginationReached = photos.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeyDao.deleteKeysByQuery(queryKey)
                    photoDao.deletePhotosByQuery(queryKey)
                }

                val prevPage = if (page == 1) null else page - 1
                val nextPage = if (endOfPaginationReached) null else page + 1

                val keys = photos.map { photo ->
                    RemoteKeyEntity(
                        photoId = photo.id,
                        queryKey = queryKey,
                        prevPage = prevPage,
                        nextPage = nextPage
                    )
                }

                val entities = photos.map { photo ->
                    val existingPhoto = photoDao.getPhotoById(photo.id)
                    photo.toEntity(
                        queryKey = queryKey,
                        pageIndex = page,
                        isFavorite = existingPhoto?.isFavorite ?: false
                    )
                }

                remoteKeyDao.insertKeys(keys)
                photoDao.insertPhotos(entities)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}