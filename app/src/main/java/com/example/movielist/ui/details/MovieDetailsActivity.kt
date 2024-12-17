package com.example.movielist.ui.details

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.movielist.R
import com.example.movielist.data.model.MovieDetails
import com.example.movielist.databinding.ActivityMovieDetailsBinding

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailsBinding
    private val movieDetailsViewModel: MovieDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("MovieDetailsActivity", "inside onCreate MovieDetailsActivity")


        val imdbID = intent.getStringExtra("IMDB_ID") ?: return
        Log.d("MovieDetailsActivity", "imdbId: ${imdbID}")

        // Observe ViewModel LiveData
        movieDetailsViewModel.movieDetails.observe(this) { movieDetails ->
            populateUI(movieDetails)
        }

        // Trigger fetching movie details
        movieDetailsViewModel.fetchMovieDetails(imdbID)
    }

    private fun populateUI(movieDetails: MovieDetails?) {
        movieDetails?.let {
            binding.movieTitleTextView.text = it.Title
            binding.movieYearTextView.text = it.Year
            binding.moviePlotTextView.text = it.Plot
            Glide.with(this)
                .load(it.Poster.takeIf { it != "N/A" } ?: R.drawable.ic_place_holder)
                .into(binding.moviePosterImageView)
        }
    }
}
