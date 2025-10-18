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
import com.example.ap7mt.ui.components.GameDetailDialog
import com.example.ap7mt.ui.components.GameList
import com.example.ap7mt.ui.components.Toolbar
import com.example.ap7mt.ui.theme.GameDatabaseTheme
import com.example.ap7mt.ui.viewmodel.FavoritesViewModel
import com.example.ap7mt.ui.viewmodel.FavoritesViewModelFactory
import com.example.ap7mt.ui.viewmodel.ViewMode

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = viewModel(
        factory = FavoritesViewModelFactory(LocalContext.current)
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    GameDatabaseTheme() {
        Scaffold(
            topBar = {
                Toolbar(
                    showClearAllFavorites = uiState.games.isNotEmpty(),
                    onClearAllFavorites = viewModel::clearAllFavorites
                )
            }
        ) { paddingValues ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
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
                            text = stringResource(R.string.no_favorite_games),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        GameList(
                            games = uiState.games,
                            viewMode = ViewMode.GRID,
                            onGameClick = viewModel::showGameDetail,
                            onFavoriteClick = viewModel::toggleFavorite
                        )
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
