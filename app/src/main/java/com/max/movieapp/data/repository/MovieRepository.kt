package com.max.movieapp.data.repository

import com.max.movieapp.data.remote.api.RetrofitInstance

class MovieRepository(
) {
    suspend fun searchMovies(pageNumber: Int) =
        RetrofitInstance.api.searchMovies(pageNumber)

}