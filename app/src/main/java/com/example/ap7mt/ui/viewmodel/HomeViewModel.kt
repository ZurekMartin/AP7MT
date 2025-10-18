package com.example.ap7mt.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ap7mt.data.model.*
import com.example.ap7mt.data.repository.FavoritesRepository
import com.example.ap7mt.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val games: List<Game> = emptyList(),
    val selectedGame: Game? = null,
    val gameDetail: Game? = null,
    val isLoading: Boolean = false,
    val isLoadingGameDetail: Boolean = false,
    val error: String? = null,
    val gameDetailError: String? = null,
    val searchQuery: String = "",
    val platform: Platform = Platform.ALL,
    val category: Category = Category.ALL,
    val sortBy: SortBy = SortBy.RELEVANCE,
    val viewMode: ViewMode = ViewMode.GRID,
    val showFilters: Boolean = true,
    val showGameDetail: Boolean = false,
    val showFilterMenu: Boolean = false,
    val tempPlatform: Platform = Platform.ALL,
    val tempCategory: Category = Category.ALL,
    val tempSortBy: SortBy = SortBy.RELEVANCE,
    val tempViewMode: ViewMode = ViewMode.GRID
)

enum class ViewMode(val displayName: String) {
    LIST("Seznam"), GRID("Mřížka")
}

class HomeViewModel(
    context: Context? = null
) : ViewModel() {

    private val repository = GameRepository()
    private val favoritesRepository = context?.let { FavoritesRepository.getInstance(it) }

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadGames()
    }

    fun loadGames() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = if (_uiState.value.searchQuery.isNotBlank()) {
                repository.searchGames(_uiState.value.searchQuery)
            } else {
                repository.getGames(
                    platform = _uiState.value.platform,
                    category = _uiState.value.category,
                    sortBy = _uiState.value.sortBy
                )
            }

            result.fold(
                onSuccess = { games ->
                    _uiState.value = _uiState.value.copy(
                        games = games,
                        isLoading = false,
                        error = null
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        games = emptyList(),
                        isLoading = false,
                        error = exception.message ?: "Neznámá chyba"
                    )
                }
            )
        }
    }

    fun searchGames(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        loadGames()
    }

    fun setPlatform(platform: Platform) {
        _uiState.value = _uiState.value.copy(platform = platform)
        if (_uiState.value.searchQuery.isBlank()) {
            loadGames()
        }
    }

    fun setCategory(category: Category) {
        _uiState.value = _uiState.value.copy(category = category)
        if (_uiState.value.searchQuery.isBlank()) {
            loadGames()
        }
    }

    fun setSortBy(sortBy: SortBy) {
        _uiState.value = _uiState.value.copy(sortBy = sortBy)
        if (_uiState.value.searchQuery.isBlank()) {
            loadGames()
        }
    }

    fun setViewMode(viewMode: ViewMode) {
        _uiState.value = _uiState.value.copy(viewMode = viewMode)
    }

    fun toggleViewMode() {
        val newViewMode = when (_uiState.value.viewMode) {
            ViewMode.LIST -> ViewMode.GRID
            ViewMode.GRID -> ViewMode.LIST
        }
        setViewMode(newViewMode)
    }

    fun toggleFiltersVisibility() {
        _uiState.value = _uiState.value.copy(showFilters = !_uiState.value.showFilters)
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

            val result = repository.getGameDetails(gameId)

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

    fun clearSearch() {
        _uiState.value = _uiState.value.copy(searchQuery = "")
        loadGames()
    }

    fun retry() {
        loadGames()
    }

    fun toggleFilterMenu() {
        val currentState = _uiState.value
        if (!currentState.showFilterMenu) {
            _uiState.value = currentState.copy(
                showFilterMenu = true,
                tempPlatform = currentState.platform,
                tempCategory = currentState.category,
                tempSortBy = currentState.sortBy,
                tempViewMode = currentState.viewMode
            )
        } else {
            // Closing menu
            _uiState.value = currentState.copy(showFilterMenu = false)
        }
    }

    fun setTempPlatform(platform: Platform) {
        _uiState.value = _uiState.value.copy(tempPlatform = platform)
    }

    fun setTempCategory(category: Category) {
        _uiState.value = _uiState.value.copy(tempCategory = category)
    }

    fun setTempSortBy(sortBy: SortBy) {
        _uiState.value = _uiState.value.copy(tempSortBy = sortBy)
    }

    fun setTempViewMode(viewMode: ViewMode) {
        _uiState.value = _uiState.value.copy(tempViewMode = viewMode)
    }

    fun toggleTempViewMode() {
        val newViewMode = when (_uiState.value.tempViewMode) {
            ViewMode.LIST -> ViewMode.GRID
            ViewMode.GRID -> ViewMode.LIST
        }
        setTempViewMode(newViewMode)
    }

    fun applyFilters() {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(
            platform = currentState.tempPlatform,
            category = currentState.tempCategory,
            sortBy = currentState.tempSortBy,
            viewMode = currentState.tempViewMode
        )
        if (currentState.searchQuery.isBlank()) {
            loadGames()
        }
    }

    fun resetFilters() {
        _uiState.value = _uiState.value.copy(
            tempPlatform = Platform.ALL,
            tempCategory = Category.ALL,
            tempSortBy = SortBy.RELEVANCE,
            tempViewMode = ViewMode.GRID
        )
    }

    fun closeFilterMenu() {
        _uiState.value = _uiState.value.copy(showFilterMenu = false)
    }

    fun toggleFavorite(game: Game) {
        favoritesRepository?.toggleFavorite(game.id)
    }
}

class HomeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
