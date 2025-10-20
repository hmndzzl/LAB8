package com.example.lab8.data.mappers

import com.example.lab8.data.local.entities.PhotoEntity
import com.example.lab8.Models.PexelsPhoto

fun PexelsPhoto.toEntity(queryKey: String, pageIndex: Int, isFavorite: Boolean = false): PhotoEntity {
    return PhotoEntity(
        id = this.id,
        width = this.width,
        height = this.height,
        url = this.url,
        photographer = this.photographer,
        photographerId = this.photographerId,
        avgColor = this.avgColor,
        srcOriginal = this.src.original,
        srcLarge = this.src.large,
        srcMedium = this.src.medium,
        srcSmall = this.src.small,
        alt = this.alt,
        isFavorite = isFavorite,
        queryKey = queryKey,
        pageIndex = pageIndex,
        updatedAt = System.currentTimeMillis()
    )
}

fun PhotoEntity.toPexelsPhoto(): PexelsPhoto {
    return PexelsPhoto(
        id = this.id,
        width = this.width,
        height = this.height,
        url = this.url,
        photographer = this.photographer,
        photographerUrl = "", // not stored
        photographerId = this.photographerId,
        avgColor = this.avgColor,
        src = com.example.lab8.Models.PexelsSrc(
            original = this.srcOriginal,
            large2x = null,
            large = this.srcLarge,
            medium = this.srcMedium,
            small = this.srcSmall,
            portrait = null,
            landscape = null,
            tiny = null
        ),
        liked = false,
        alt = this.alt
    )
}

// Normalize query for consistency
fun String.normalizeQuery(): String {
    return this.trim().lowercase().ifEmpty { "curated" }
}