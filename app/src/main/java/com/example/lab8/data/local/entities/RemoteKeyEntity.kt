package com.example.lab8.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeyEntity(
    @PrimaryKey
    val photoId: Long,
    val queryKey: String,
    val prevPage: Int?,
    val nextPage: Int?
)