package com.example.ap7mt.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.ap7mt.data.model.Game
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailDialog(
    game: Game?,
    selectedGame: Game?,
    isLoading: Boolean,
    error: String?,
    onDismiss: () -> Unit
) {
    val uriHandler = LocalUriHandler.current

    val displayGame = game ?: selectedGame

    if (displayGame == null) {
        onDismiss()
        return
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Chyba při načítání detailu",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onDismiss) {
                            Text("Zavřít")
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = displayGame.title,
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.align(Alignment.CenterStart)
                            )

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Zavřít"
                                )
                            }
                        }

                        if (game != null && game.screenshots.isNotEmpty()) {
                            ScreenshotGallery(screenshots = game.screenshots)
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                AssistChip(
                                    onClick = {},
                                    label = { Text(displayGame.genre) }
                                )
                                AssistChip(
                                    onClick = {},
                                    label = { Text(displayGame.platform) }
                                )
                                if (game != null && game.status.isNotBlank()) {
                                    AssistChip(
                                        onClick = {},
                                        label = { Text("Status: ${game.status}") }
                                    )
                                }
                            }

                            if (displayGame.shortDescription.isNotBlank()) {
                                Text(
                                    text = "Krátký popis",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = displayGame.shortDescription,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            if (displayGame.developer.isNotBlank()) {
                                Text(
                                    text = "Vývojář",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = displayGame.developer,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            if (displayGame.publisher.isNotBlank()) {
                                Text(
                                    text = "Vydavatel",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = displayGame.publisher,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            if (displayGame.releaseDate.isNotBlank()) {
                                Text(
                                    text = "Datum vydání",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = displayGame.releaseDate,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            if (game != null && game.description.isNotBlank()) {
                                Text(
                                    text = "Detailní popis",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = game.description,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            game?.minimumSystemRequirements?.let { requirements ->
                                Text(
                                    text = "Minimální požadavky na systém",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                requirements.os?.let {
                                    Text("OS: $it", style = MaterialTheme.typography.bodyMedium)
                                }
                                requirements.processor?.let {
                                    Text("Procesor: $it", style = MaterialTheme.typography.bodyMedium)
                                }
                                requirements.memory?.let {
                                    Text("Paměť: $it", style = MaterialTheme.typography.bodyMedium)
                                }
                                requirements.graphics?.let {
                                    Text("Grafika: $it", style = MaterialTheme.typography.bodyMedium)
                                }
                                requirements.storage?.let {
                                    Text("Úložiště: $it", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }

                        OutlinedButton(
                            onClick = { uriHandler.openUri(displayGame.gameUrl) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("Hrát hru")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ScreenshotGallery(screenshots: List<com.example.ap7mt.data.model.Screenshot>) {
    if (screenshots.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { Int.MAX_VALUE })
    val coroutineScope = rememberCoroutineScope()
    val actualPage = remember { derivedStateOf { pagerState.currentPage % screenshots.size } }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val screenshotIndex = page % screenshots.size
            AsyncImage(
                model = screenshots[screenshotIndex].image,
                contentDescription = "Screenshot ${screenshotIndex + 1}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        IconButton(
            onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                }
            },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Předchozí"
            )
        }

        IconButton(
            onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Další"
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(screenshots.size) { index ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = if (index == actualPage.value)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                )
            }
        }
    }
}
