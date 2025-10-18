package com.example.ap7mt.data.api

import com.example.ap7mt.data.model.Game
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FreeToGameApi {

    @GET("games")
    suspend fun getGames(
        @Query("platform") platform: String? = null,
        @Query("category") category: String? = null,
        @Query("sort-by") sortBy: String? = null
    ): Response<List<Game>>

    @GET("game")
    suspend fun getGameDetails(@Query("id") gameId: Int): Response<Game>

    @GET("filter")
    suspend fun filterGames(
        @Query("tag") tag: String? = null,
        @Query("platform") platform: String? = null,
        @Query("sort") sort: String? = null
    ): Response<List<Game>>

    companion object {
        const val BASE_URL = "https://www.freetogame.com/api/"
    }
}
