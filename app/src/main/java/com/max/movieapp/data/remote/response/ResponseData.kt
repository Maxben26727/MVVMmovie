package com.max.movieapp.data.remote.response

data class ResponseData(
    val Response: String,
    val Search: MutableList<Search>,
    val totalResults: String
)