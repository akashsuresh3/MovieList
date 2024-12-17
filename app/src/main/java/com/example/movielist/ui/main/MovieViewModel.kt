package com.example.movielist.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movielist.data.api.RetrofitInstance
import com.example.movielist.data.model.Movie
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {

    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>> get() = _movies

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var currentPage = 1
    private var currentSearchTerm: String = ""
    private val apiKey = "e515d2e8" // OMDb API key

    fun isLoading() = _isLoading.value == true

    /**
     * Fetch movies based on the search term. Resets pagination if the term changes.
     */
    fun fetchMovies(searchTerm: String) {
        if (isLoading()) return

        // Reset pagination if the search term changes
        if (currentSearchTerm != searchTerm) {
            currentSearchTerm = searchTerm
            currentPage = 1
            _movies.postValue(emptyList()) // Clear existing movies
        }

        loadMovies()
    }

    /**
     * Load the next page of movies for the current search term.
     */
    fun loadNextPage() {
        if (isLoading() || currentSearchTerm.isEmpty()) return

        _isLoading.postValue(true)

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.searchMovies(currentSearchTerm, currentPage, apiKey)
                if (response.Response == "True" && response.Search != null) {
                    val newMovies = response.Search
                    Log.d("MovieViewModel", "Size of newMovies = ${newMovies.size}")
                    val updatedMovies = (_movies.value ?: emptyList()) + newMovies
                    Log.d("MovieViewModel", "Size of updatedMovies = ${updatedMovies.size}")
                    _movies.postValue(updatedMovies)
                    currentPage++
                }
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error fetching movies: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    /**
     * Internal method to handle API calls for fetching movies.
     */
    private fun loadMovies() {
        _isLoading.postValue(true)

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.searchMovies(currentSearchTerm, currentPage, apiKey)
                if (response.Response == "True" && response.Search != null) {
                    val newMovies = response.Search
                    val updatedMovies = (_movies.value ?: emptyList()) + newMovies
                    _movies.postValue(updatedMovies)
                    currentPage++
                } else {
                    _error.postValue(response.Error ?: "No more results found.")
                }
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error fetching movies: ${e.message}")
                _error.postValue("Error fetching movies. Please try again.")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}



//import android.util.Log
//import androidx.lifecycle.*
//import com.example.movielist.data.model.Movie
//import com.example.movielist.data.repository.MovieRepository
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//class MovieViewModel @Inject constructor(
//    private val repository: MovieRepository // Injected MovieRepository
//) : ViewModel() {
//
//    private val _movies = MutableLiveData<List<Movie>>()
//    val movies: LiveData<List<Movie>> get() = _movies
//
//    private val _error = MutableLiveData<String?>()
//    val error: LiveData<String?> get() = _error
//
//    private val _isLoading = MutableLiveData(false)
//    val isLoading: LiveData<Boolean> get() = _isLoading
//
//    private var currentPage = 1
//    private var currentSearchTerm: String = ""
//    private val apiKey = "e515d2e8" // OMDb API key
//
//    /**
//     * Fetch movies based on the search term. Resets pagination if the term changes.
//     */
//    fun fetchMovies(searchTerm: String) {
//        if (_isLoading.value == true) return
//
//        // Reset pagination if the search term changes
//        if (currentSearchTerm != searchTerm) {
//            currentSearchTerm = searchTerm
//            currentPage = 1
//            _movies.postValue(emptyList()) // Clear existing movies
//        }
//
//        loadMovies()
//    }
//
//    /**
//     * Load the next page of movies for the current search term.
//     */
//    fun loadNextPage() {
//        if (_isLoading.value == true || currentSearchTerm.isEmpty()) return
//        loadMovies()
//    }
//
//    /**
//     * Internal method to handle API calls for fetching movies.
//     */
//    private fun loadMovies() {
//        _isLoading.postValue(true)
//
//        viewModelScope.launch {
//            try {
//                // Fetch data from repository
//                val response = repository.getMovies(currentSearchTerm, currentPage, apiKey)
//                if (response.isSuccessful && response.body() != null) {
//                    val newMovies = response.body()?.Search ?: emptyList()
//                    val updatedMovies = (_movies.value ?: emptyList()) + newMovies
//                    _movies.postValue(updatedMovies)
//                    currentPage++
//                } else {
//                    _error.postValue(response.body()?.Error ?: "No more results found.")
//                }
//            } catch (e: Exception) {
//                Log.e("MovieViewModel", "Error fetching movies: ${e.message}")
//                _error.postValue("Error fetching movies. Please try again.")
//            } finally {
//                _isLoading.postValue(false)
//            }
//        }
//    }
//}
