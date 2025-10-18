package com.example.ap7mt.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ap7mt.data.model.Game
import com.example.ap7mt.data.repository.FavoritesRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameCard(
    game: Game,
    onGameClick: (Game) -> Unit,
    modifier: Modifier = Modifier,
    onFavoriteClick: ((Game) -> Unit)? = null
) {
    val context = LocalContext.current
    val favoritesRepository = FavoritesRepository.getInstance(context)
    val favorites by favoritesRepository.favorites.collectAsState()
    val isFavorite = favorites.contains(game.id)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onGameClick(game) },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column {
            Box {
                AsyncImage(
                    model = game.thumbnail,
                    contentDescription = game.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(MaterialTheme.shapes.large),
                    contentScale = ContentScale.Crop
                )

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(40.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.9f),
                    shadowElevation = 4.dp
                ) {
                    IconButton(
                        onClick = {
                            if (onFavoriteClick != null) {
                                onFavoriteClick(game)
                            } else {
                                favoritesRepository.toggleFavorite(game.id)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (isFavorite) "Odebrat z oblíbených" else "Přidat do oblíbených",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = game.shortDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = {},
                        label = { Text(game.genre) },
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    AssistChip(
                        onClick = {},
                        label = { Text(game.platform) }
                    )
                }
            }
        }
    }
}
