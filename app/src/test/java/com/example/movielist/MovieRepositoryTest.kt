package com.example.movielist.data.repository

import com.example.movielist.data.api.MovieApi
import com.example.movielist.data.model.MovieResponse
import com.example.movielist.data.repository.MovieRepository
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import retrofit2.Response

class MovieRepositoryTest {
    private lateinit var repository: MovieRepository
    private val mockApi = mock(MovieApi::class.java)

    @Before
    fun setup() {
        repository = MovieRepository(mockApi)
    }

    @Test
    fun `test getMovies success`() = runBlocking {
        val fakeResponse = MovieResponse(listOf(), "True", null)
        `when`(mockApi.searchMovies("Batman", 1, "test_api_key"))
            .thenReturn(Response.success(fakeResponse))

        val result = repository.getMovies("Batman", 1, "test_api_key")

        assertEquals(true, result.isSuccessful)
        assertEquals(fakeResponse, result.body())
    }

    @Test
    fun `test getMovies failure`() = runBlocking {
        val errorResponse = Response.error<MovieResponse>(
            500,
            ResponseBody.create("application/json".toMediaType(), "Internal Server Error")
        )
        `when`(mockApi.searchMovies("Batman", 1, "test_api_key"))
            .thenReturn(errorResponse)

        val result = repository.getMovies("Batman", 1, "test_api_key")

        assertEquals(500, result.code())
        assertEquals("Internal Server Error", result.errorBody()?.string())
    }
}
