package com.example.ap7mt.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FavoritesRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _favorites = MutableStateFlow<Set<Int>>(loadFavorites())
    val favorites: StateFlow<Set<Int>> = _favorites.asStateFlow()

    private fun loadFavorites(): Set<Int> {
        val json = prefs.getString(FAVORITES_KEY, null)
        return if (json != null) {
            try {
                gson.fromJson(json, object : TypeToken<Set<Int>>() {}.type) ?: emptySet()
            } catch (e: Exception) {
                emptySet()
            }
        } else {
            emptySet()
        }
    }

    private fun saveFavorites(favorites: Set<Int>) {
        val json = gson.toJson(favorites)
        prefs.edit().putString(FAVORITES_KEY, json).apply()
        _favorites.value = favorites
    }

    fun addToFavorites(gameId: Int) {
        val currentFavorites = _favorites.value.toMutableSet()
        currentFavorites.add(gameId)
        saveFavorites(currentFavorites)
    }

    fun removeFromFavorites(gameId: Int) {
        val currentFavorites = _favorites.value.toMutableSet()
        currentFavorites.remove(gameId)
        saveFavorites(currentFavorites)
    }

    fun isFavorite(gameId: Int): Boolean {
        return _favorites.value.contains(gameId)
    }

    fun toggleFavorite(gameId: Int) {
        if (isFavorite(gameId)) {
            removeFromFavorites(gameId)
        } else {
            addToFavorites(gameId)
        }
    }

    fun clearAllFavorites() {
        saveFavorites(emptySet())
    }

    companion object {
        private const val FAVORITES_KEY = "favorite_games"

        @Volatile
        private var instance: FavoritesRepository? = null

        fun getInstance(context: Context): FavoritesRepository {
            return instance ?: synchronized(this) {
                instance ?: FavoritesRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}
