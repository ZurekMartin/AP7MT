package com.example.ap7mt.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ap7mt.ui.viewmodel.ViewMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Toolbar(
    showClearAllFavorites: Boolean = false,
    onClearAllFavorites: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text("GameDatabase") },
        actions = {
            if (showClearAllFavorites && onClearAllFavorites != null) {
                IconButton(onClick = onClearAllFavorites) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Smazat všechny oblíbené"
                    )
                }
            }
        },
        modifier = modifier
    )
}
