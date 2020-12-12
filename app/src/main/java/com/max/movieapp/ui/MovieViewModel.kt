package com.max.movieapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.max.movieapp.MovieApplication
import com.max.movieapp.data.remote.response.ResponseData
import com.max.movieapp.data.remote.response.Search
import com.max.movieapp.data.repository.MovieRepository
import com.max.movieapp.util.Resource
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

class MovieViewModel(
    app: Application,
    private val movieRepository: MovieRepository
) : AndroidViewModel(app) {

    val searchMovies: MutableLiveData<Resource<ResponseData>> = MutableLiveData()
    var searchMoviesPage = 1
    var searchMoviesResponse: ResponseData? = null


    init {
        searchMovies()
    }



    fun searchMovies() = viewModelScope.launch {
        safeSearchMoviesCall()
    }


    private fun handleSearchMoviesResponse(response: Response<ResponseData>) : Resource<ResponseData> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if(searchMoviesResponse == null) {
                    searchMoviesResponse = resultResponse
                } else {
                    val oldArticles:MutableList<Search> = searchMoviesResponse!!.Search
                    val newArticles:MutableList<Search> = resultResponse.Search
                    oldArticles.addAll(newArticles)
                }
                return Resource.Success(searchMoviesResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }




    private suspend fun safeSearchMoviesCall() {
        searchMovies.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()) {
                val response = movieRepository.searchMovies(searchMoviesPage)
                searchMovies.postValue(handleSearchMoviesResponse(response))
            } else {
                searchMovies.postValue(Resource.Error("No internet connection"))
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> searchMovies.postValue(Resource.Error("Network Failure"))
                else -> searchMovies.postValue(Resource.Error("Conversion Error"))
            }
        }
    }


    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<MovieApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}












