package com.example.movielist.data.api

import com.example.movielist.data.model.MovieDetails
import com.example.movielist.data.model.MovieResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {

    @GET("/")
    suspend fun searchMovies(
        @Query("s") searchTerm: String,
        @Query("page") page: Int,
        @Query("apikey") apiKey: String
    ): MovieResponse



    @GET("/")
    suspend fun getMovieDetails(
        @Query("i") imdbID: String,
        @Query("apikey") apiKey: String
    ): Response<MovieDetails>

}
