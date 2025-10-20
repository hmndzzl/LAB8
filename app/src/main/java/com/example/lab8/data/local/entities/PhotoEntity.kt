package com.example.lab8.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "photos",
    indices = [
        Index(value = ["queryKey", "pageIndex"]),
        Index(value = ["isFavorite"])
    ]
)
data class PhotoEntity(
    @PrimaryKey
    val id: Long,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    val photographerId: Long,
    val avgColor: String?,
    val srcOriginal: String,
    val srcLarge: String?,
    val srcMedium: String?,
    val srcSmall: String?,
    val alt: String?,
    val isFavorite: Boolean = false,
    val queryKey: String, // normalized query (e.g., "curated", "nature", etc.)
    val pageIndex: Int,
    val updatedAt: Long = System.currentTimeMillis()
)