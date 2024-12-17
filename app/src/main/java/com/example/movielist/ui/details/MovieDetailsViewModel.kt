package com.example.movielist.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movielist.data.api.RetrofitInstance
import com.example.movielist.data.model.MovieDetails
import kotlinx.coroutines.launch

class MovieDetailsViewModel : ViewModel() {

    private val _movieDetails = MutableLiveData<MovieDetails?>()
    val movieDetails: LiveData<MovieDetails?> get() = _movieDetails

    private val apiKey = "e515d2e8" // Replace with your API key

    fun fetchMovieDetails(imdbID: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getMovieDetails(imdbID, apiKey)
                if (response.isSuccessful) {
                    _movieDetails.postValue(response.body())
                } else {
                    _movieDetails.postValue(null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _movieDetails.postValue(null)
            }
        }
    }
}
