package com.example.movielist.data.model

data class MovieResponse(
    val Search: List<Movie>,
    val totalResults: String,
    val Response: String,
    val Error: String? = null // Optional error message from the API
)

data class Movie(
    val Title: String,
    val Year: String,
    val imdbID: String,
    val Type: String,
    val Poster: String
)