package com.example.ap7mt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ap7mt.R
import com.example.ap7mt.ui.components.FilterMenu
import com.example.ap7mt.ui.components.GameDetailDialog
import com.example.ap7mt.ui.components.GameList
import com.example.ap7mt.ui.components.SearchBar
import com.example.ap7mt.ui.components.Toolbar
import com.example.ap7mt.ui.theme.GameDatabaseTheme
import com.example.ap7mt.ui.viewmodel.HomeViewModel
import com.example.ap7mt.ui.viewmodel.HomeViewModelFactory
import com.example.ap7mt.ui.viewmodel.ViewMode

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(LocalContext.current)
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    GameDatabaseTheme() {
        Scaffold(
            topBar = {
                Toolbar()
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::searchGames,
                    onClearSearch = viewModel::clearSearch,
                    onToggleFilterMenu = viewModel::toggleFilterMenu
                )

                FilterMenu(
                    expanded = uiState.showFilterMenu,
                    onDismiss = viewModel::closeFilterMenu,
                    selectedPlatform = uiState.tempPlatform,
                    selectedCategory = uiState.tempCategory,
                    selectedSortBy = uiState.tempSortBy,
                    selectedViewMode = uiState.tempViewMode,
                    onPlatformChange = viewModel::setTempPlatform,
                    onCategoryChange = viewModel::setTempCategory,
                    onSortByChange = viewModel::setTempSortBy,
                    onViewModeChange = viewModel::setTempViewMode,
                    onApplyFilters = viewModel::applyFilters,
                    onResetFilters = viewModel::resetFilters
                )

                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        uiState.isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        uiState.error != null -> {
                            ErrorContent(
                                error = uiState.error!!,
                                onRetry = viewModel::retry,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        uiState.games.isEmpty() -> {
                            Text(
                                text = stringResource(R.string.no_games_found),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        else -> {
                            GameList(
                                games = uiState.games,
                                viewMode = uiState.viewMode,
                                onGameClick = viewModel::showGameDetail,
                                onFavoriteClick = viewModel::toggleFavorite
                            )
                        }
                    }
                }
            }
        }

        if (uiState.showGameDetail) {
            GameDetailDialog(
                game = uiState.gameDetail,
                selectedGame = uiState.selectedGame,
                isLoading = uiState.isLoadingGameDetail,
                error = uiState.gameDetailError,
                onDismiss = viewModel::hideGameDetail
            )
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.error),
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium
        )
        Button(onClick = onRetry) {
            Text(stringResource(R.string.retry))
        }
    }
}
