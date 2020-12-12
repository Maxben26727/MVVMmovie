package com.max.movieapp.ui.searchMovies

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.max.movieapp.R
import com.max.movieapp.adapters.MovieAdapter
import com.max.movieapp.data.repository.MovieRepository
import com.max.movieapp.databinding.ActivitySearchBinding
import com.max.movieapp.ui.MovieViewModel
import com.max.movieapp.ui.MoviesViewModelProviderFactory
import com.max.movieapp.util.Constants
import com.max.movieapp.util.Resource

class SearchActivity : AppCompatActivity() {

    lateinit var viewModel: MovieViewModel
    private lateinit var movieAdapter:MovieAdapter
    private var _binding:ActivitySearchBinding ? =null
    private val binding:ActivitySearchBinding get() =  _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()

        val movieRepository = MovieRepository()
        val viewModelProviderFactory = MoviesViewModelProviderFactory(application, movieRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(MovieViewModel::class.java)


        viewModel.searchMovies.observe(this, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { MovieResponse ->
                        if(MovieResponse.Search.size>0)
                        movieAdapter.differ.submitList(MovieResponse.Search.toList())
                        val totalPages = MovieResponse.totalResults.toInt() / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchMoviesPage == totalPages
                        if(isLastPage) {
                            binding.rv.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(this, "An error occured: $message", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }



private fun hideProgressBar() {
    binding.paginationProgressBar.visibility = View.INVISIBLE
    isLoading = false
}

private fun showProgressBar() {
    binding.paginationProgressBar.visibility = View.VISIBLE
    isLoading = true
}

var isLoading = false
var isLastPage = false
var isScrolling = false

private val scrollListener = object : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount

        val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
        val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
        val isNotAtBeginning = firstVisibleItemPosition >= 0
        val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
        val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                isTotalMoreThanVisible && isScrolling
        if (isAtLastItem) {
            viewModel.searchMoviesPage++
            viewModel.searchMovies()
            isScrolling = false
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            isScrolling = true
        }
    }
}


private fun setupRecyclerView() {
    movieAdapter = MovieAdapter(applicationContext)
    binding.rv.apply {
        setHasFixedSize(true)
        adapter = movieAdapter
        addOnScrollListener(scrollListener)
    }
}
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
