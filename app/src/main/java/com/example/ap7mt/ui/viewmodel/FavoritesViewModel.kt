package com.example.ap7mt.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ap7mt.data.model.Game
import com.example.ap7mt.data.repository.FavoritesRepository
import com.example.ap7mt.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val games: List<Game> = emptyList(),
    val selectedGame: Game? = null,
    val gameDetail: Game? = null,
    val isLoading: Boolean = false,
    val isLoadingGameDetail: Boolean = false,
    val error: String? = null,
    val gameDetailError: String? = null,
    val showGameDetail: Boolean = false
)

class FavoritesViewModel(
    context: Context,
    private val gameRepository: GameRepository = GameRepository()
) : ViewModel() {
    private val favoritesRepository = FavoritesRepository.getInstance(context)
    private val allGamesFlow = MutableStateFlow<List<Game>>(emptyList())

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavoriteGames()
    }

    private fun loadFavoriteGames() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val result = gameRepository.getGames()
                result.fold(
                    onSuccess = { allGames ->
                        allGamesFlow.value = allGames

                        favoritesRepository.favorites.collect { favoriteIds ->
                            val favoriteGames = allGames.filter { it.id in favoriteIds }
                            _uiState.value = _uiState.value.copy(
                                games = favoriteGames,
                                isLoading = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            games = emptyList(),
                            isLoading = false,
                            error = exception.message ?: "Neznámá chyba"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    games = emptyList(),
                    isLoading = false,
                    error = e.message ?: "Neznámá chyba"
                )
            }
        }
    }

    fun showGameDetail(game: Game) {
        _uiState.value = _uiState.value.copy(
            selectedGame = game,
            showGameDetail = true,
            gameDetail = null,
            gameDetailError = null
        )
        loadGameDetail(game.id)
    }

    fun hideGameDetail() {
        _uiState.value = _uiState.value.copy(
            selectedGame = null,
            gameDetail = null,
            showGameDetail = false,
            isLoadingGameDetail = false,
            gameDetailError = null
        )
    }

    private fun loadGameDetail(gameId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingGameDetail = true, gameDetailError = null)

            val result = gameRepository.getGameDetails(gameId)

            result.fold(
                onSuccess = { gameDetail ->
                    _uiState.value = _uiState.value.copy(
                        gameDetail = gameDetail,
                        isLoadingGameDetail = false,
                        gameDetailError = null
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        gameDetail = null,
                        isLoadingGameDetail = false,
                        gameDetailError = exception.message ?: "Chyba při načítání detailu hry"
                    )
                }
            )
        }
    }

    fun toggleFavorite(game: Game) {
        favoritesRepository.toggleFavorite(game.id)
    }

    fun clearAllFavorites() {
        favoritesRepository.clearAllFavorites()
    }

    fun retry() {
        loadFavoriteGames()
    }
}

class FavoritesViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
