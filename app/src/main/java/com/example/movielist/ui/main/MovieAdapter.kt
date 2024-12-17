//package com.example.movielist
//
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//
//
//class MovieAdapter(private var movieList: List<Movie>, private val onItemClick: (String) -> Unit) :
//    RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {
//
//    fun updateData(newMovies: List<Movie>) {
//        movieList = newMovies
//        notifyDataSetChanged() // Notify RecyclerView to refresh
//    }
//
//    inner class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val moviePosterImageView: ImageView = view.findViewById(R.id.moviePosterImageView)
//        val movieTitleTextView: TextView = view.findViewById(R.id.movieTitleTextView)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
//        return MovieViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
//        val movie = movieList[position]
//        Log.d("MovieAdapter", movie.Title)
//        Log.d("MovieAdapter", movie.Poster)
//        holder.movieTitleTextView.text = movie.Title
//        Glide.with(holder.moviePosterImageView.context)
//            .load(movie.Poster)
//            .into(holder.moviePosterImageView)
//
//        Glide.with(holder.moviePosterImageView.context)
//            .load(movie.Poster.takeIf { it != "N/A" } ?: R.drawable.ic_place_holder)
//            .into(holder.moviePosterImageView)
//
//
//        holder.itemView.setOnClickListener { onItemClick(movie.imdbID) }
//    }
//
//    override fun getItemCount(): Int = movieList.size
//
//
//}
//
//
package com.example.movielist.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movielist.R
import com.example.movielist.data.model.Movie

class MovieAdapter(
    private var movieList: MutableList<Movie>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADER = 1
    private var isLoading = false

    // Refresh the entire movie list (used for new searches)
    fun refreshMovies(newMovies: List<Movie>) {
        movieList.clear()
        movieList.addAll(newMovies)
        notifyDataSetChanged()
    }

    // Add more movies to the list (used for pagination)
    fun addMovies(newMovies: List<Movie>) {
        val startPosition = movieList.size
        movieList.clear()
        movieList.addAll(newMovies)
        notifyItemRangeInserted(startPosition, newMovies.size)
    }

    fun updateData(newMovies: List<Movie>) {
        val startPosition = movieList.size
        movieList.addAll(newMovies)
        notifyItemRangeInserted(startPosition, newMovies.size)
    }

    fun showLoader() {
        if (!isLoading) {
            isLoading = true
            movieList.add(Movie("", "", "", "", ""))
            notifyItemInserted(movieList.size - 1)
        }
    }

    fun hideLoader() {
        if (isLoading) {
            isLoading = false
            val position = movieList.size - 1
            if (position >= 0 && movieList[position].imdbID.isEmpty()) {
                movieList.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }

    inner class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val moviePosterImageView: ImageView = view.findViewById(R.id.moviePosterImageView)
        val movieTitleTextView: TextView = view.findViewById(R.id.movieTitleTextView)
    }

    inner class LoaderViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
            MovieViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loader, parent, false)
            LoaderViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MovieViewHolder) {
            val movie = movieList[position]
            holder.movieTitleTextView.text = movie.Title
            Glide.with(holder.moviePosterImageView.context)
                .load(movie.Poster.takeIf { it != "N/A" } ?: R.drawable.ic_place_holder)
                .into(holder.moviePosterImageView)

            holder.itemView.setOnClickListener { onItemClick(movie.imdbID) }
        }
    }

    override fun getItemCount(): Int = movieList.size

    override fun getItemViewType(position: Int): Int {
        return if (isLoading && position == movieList.size - 1) VIEW_TYPE_LOADER else VIEW_TYPE_ITEM
    }
}



