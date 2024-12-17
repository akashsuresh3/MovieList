package com.example.movielist.data.repository

import com.example.movielist.data.api.MovieApi
import com.example.movielist.data.api.RetrofitInstance
import com.example.movielist.data.model.MovieDetails
import com.example.movielist.data.model.MovieResponse
import retrofit2.Response

class MovieRepository () {
    // Fetch movies and wrap the MovieResponse in a Response object manually
    suspend fun getMovies(searchQuery: String, page: Int = 1, apiKey: String): Response<MovieResponse> {
        return try {
            val response = RetrofitInstance.api.searchMovies(searchQuery, page, apiKey)
            Response.success(response)  // Wrap the MovieResponse as a successful Response
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, e.message ?: "Unknown error"))
        }
    }

    // Fetch movie details directly (already returns Response)
    suspend fun getMovieDetails(movieId: String, apiKey: String): Response<MovieDetails> {
        return RetrofitInstance.api.getMovieDetails(movieId, apiKey)
    }
}
