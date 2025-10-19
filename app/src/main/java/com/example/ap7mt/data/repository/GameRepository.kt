package com.example.ap7mt.data.repository

import com.example.ap7mt.data.api.ApiClient
import com.example.ap7mt.data.model.Game
import com.example.ap7mt.data.model.Platform
import com.example.ap7mt.data.model.Category
import com.example.ap7mt.data.model.SortBy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class GameRepository {

    private val api = ApiClient.api

    suspend fun getGames(
        platform: Platform = Platform.ALL,
        category: Category = Category.ALL,
        sortBy: SortBy = SortBy.RELEVANCE
    ): Result<List<Game>> = withContext(Dispatchers.IO) {
        try {
            val platformParam = if (platform == Platform.ALL) null else platform.apiValue
            val categoryParam = if (category == Category.ALL) null else category.apiValue
            val sortParam = sortBy.apiValue

            val response = api.getGames(
                platform = platformParam,
                category = categoryParam,
                sortBy = sortParam
            )

            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGameDetails(gameId: Int): Result<Game> = withContext(Dispatchers.IO) {
        try {
            val response = api.getGameDetails(gameId)
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchGames(query: String): Result<List<Game>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getGames()
            val games = handleResponse(response).getOrNull() ?: emptyList()

            val filteredGames = games.filter { game ->
                game.title.contains(query, ignoreCase = true) ||
                game.genre.contains(query, ignoreCase = true) ||
                game.shortDescription.contains(query, ignoreCase = true) ||
                game.publisher.contains(query, ignoreCase = true) ||
                game.developer.contains(query, ignoreCase = true)
            }

            Result.success(filteredGames)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun <T> handleResponse(response: Response<T>): Result<T> {
        return if (response.isSuccessful) {
            response.body()?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Empty response body"))
        } else {
            Result.failure(Exception("API Error: ${response.code()} ${response.message()}"))
        }
    }
}
