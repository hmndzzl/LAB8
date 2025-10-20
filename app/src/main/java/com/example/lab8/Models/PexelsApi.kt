package com.example.lab8.Models

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PexelsApi {
    @GET("v1/curated")
    suspend fun getCurated(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): PexelsResponse

    @GET("v1/search")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): PexelsResponse
}