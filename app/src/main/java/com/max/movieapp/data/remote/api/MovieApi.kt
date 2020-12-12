package com.max.movieapp.data.remote.api

import com.max.movieapp.data.remote.response.ResponseData
import com.max.movieapp.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {

    @GET("/")
    suspend fun searchMovies(
        @Query("page")
        pageNumber: Int = 1 ,
        @Query("s")
        search: String = "new",
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<ResponseData>


}