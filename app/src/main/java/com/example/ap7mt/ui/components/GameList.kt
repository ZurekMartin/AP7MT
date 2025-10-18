package com.example.ap7mt.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ap7mt.data.model.Game
import com.example.ap7mt.ui.viewmodel.ViewMode

@Composable
fun GameList(
    games: List<Game>,
    viewMode: ViewMode,
    onGameClick: (Game) -> Unit,
    modifier: Modifier = Modifier
) {
    when (viewMode) {
        ViewMode.LIST -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(games) { game ->
                    GameListItem(
                        game = game,
                        onGameClick = onGameClick
                    )
                }
            }
        }
        ViewMode.GRID -> {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 300.dp),
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(games) { game ->
                    GameCard(
                        game = game,
                        onGameClick = onGameClick
                    )
                }
            }
        }
    }
}
