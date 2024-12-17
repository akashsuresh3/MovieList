//package com.example.movielist
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.inputmethod.EditorInfo
//
//// key : e515d2e8
//
//import androidx.activity.viewModels
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.movielist.databinding.ActivityMainBinding
//
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityMainBinding
//    private val movieViewModel: MovieViewModel by viewModels()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val adapter = MovieAdapter(emptyList()) { imdbID ->
//            val intent = Intent(this, MovieDetailsActivity::class.java)
//            intent.putExtra("IMDB_ID", imdbID)
//            startActivity(intent)
//        }
//
//
//        binding.movieRecyclerView.layoutManager = LinearLayoutManager(this)
//        binding.movieRecyclerView.adapter = adapter
//
//        // Observe ViewModel
////        movieViewModel.movies.observe(this) { movies ->
////            (binding.movieRecyclerView.adapter as MovieAdapter).apply {
////                (this as MovieAdapter).notifyDataSetChanged()
////            }
////        }
//
//        // Observe ViewModel
//        movieViewModel.movies.observe(this) { movies ->
//            // Update the adapter with new data
//            Log.d("MainActivity", "Movies received: ${movies.size}")
//            (binding.movieRecyclerView.adapter as MovieAdapter).apply {
//                (this as MovieAdapter).updateData(movies)
//            }
//        }
//
//
//
//        // Fetch movies on search
////        binding.searchEditText.setOnEditorActionListener { v, actionId, event ->
////            val searchTerm = binding.searchEditText.text.toString()
////            android.util.Log.d("MainActivity", searchTerm)
////            if (searchTerm.isNotEmpty()) {
////                movieViewModel.fetchMovies(searchTerm)
////            }
////            true
////        }
//
//        // Trigger API call on search
//        binding.searchEditText.setOnEditorActionListener { v, actionId, event ->
////            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
////                val searchTerm = binding.searchEditText.text.toString()
////                android.util.Log.d("MainActivity", searchTerm)
////                if (searchTerm.isNotEmpty()) {
////                    movieViewModel.fetchMovies(searchTerm)
////                }
////                true
////            } else {
////                false
////            }
//
//            val searchTerm = binding.searchEditText.text.toString()
//            android.util.Log.d("MainActivity", searchTerm)
//            if (searchTerm.isNotEmpty()) {
//                movieViewModel.fetchMovies(searchTerm)
//            }
//            true
//        }
//    }
//}


package com.example.movielist.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movielist.ui.details.MovieDetailsActivity
import com.example.movielist.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val movieViewModel: MovieViewModel by viewModels()
    private lateinit var adapter: MovieAdapter
    private var isLoading = false // To prevent multiple API calls simultaneously

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearch()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = MovieAdapter(mutableListOf()) { imdbID ->
            val intent = Intent(this, MovieDetailsActivity::class.java)
            intent.putExtra("IMDB_ID", imdbID)
            startActivity(intent)
        }

        binding.movieRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.movieRecyclerView.adapter = adapter

        // Add pagination scroll listener
//        binding.movieRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//
//                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//                val totalItemCount = layoutManager.itemCount
//                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
//
//                if (!isLoading && lastVisibleItemPosition + 5 >= totalItemCount) {
//                    isLoading = true
//                    movieViewModel.loadNextPage()
//                }
//            }
//        })

        binding.movieRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (!movieViewModel.isLoading() && totalItemCount <= (lastVisibleItem + 2)) {
                    movieViewModel.loadNextPage()
                }
            }
        })

        // Observe loading state to show or hide the loader
        movieViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                (binding.movieRecyclerView.adapter as MovieAdapter).showLoader()
            } else {
                (binding.movieRecyclerView.adapter as MovieAdapter).hideLoader()
            }
        }

    }

    private fun setupSearch() {
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            val searchTerm = binding.searchEditText.text.toString().trim()
            Log.d("MainActivity", "searchTerm: ${searchTerm}")
            if (searchTerm.isNotEmpty()) {
                adapter.refreshMovies(emptyList()) // Clear previous search results
                movieViewModel.fetchMovies(searchTerm) // Fetch new search results
            }
            true
        }
    }

    private fun observeViewModel() {
        movieViewModel.movies.observe(this) { movies ->
            Log.d("MainActivity", "Movies received: ${movies.size}")
            adapter.addMovies(movies) // Append new movies to the adapter
            isLoading = false
        }

        movieViewModel.error.observe(this) { errorMessage ->
            Log.e("MainActivity", "Error: $errorMessage")
            isLoading = false
        }
    }
}

