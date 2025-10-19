package com.example.ap7mt.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ap7mt.data.model.Platform
import com.example.ap7mt.data.model.Category
import com.example.ap7mt.data.model.SortBy
import com.example.ap7mt.ui.viewmodel.ViewMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    selectedPlatform: Platform,
    selectedCategory: Category,
    selectedSortBy: SortBy,
    selectedViewMode: ViewMode,
    onPlatformChange: (Platform) -> Unit,
    onCategoryChange: (Category) -> Unit,
    onSortByChange: (SortBy) -> Unit,
    onViewModeChange: (ViewMode) -> Unit,
    onApplyFilters: () -> Unit,
    onResetFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (expanded) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Card(
                modifier = modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.9f)
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Filtry",
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

                    Spacer(modifier = Modifier.height(16.dp))

                    FilterDropdown(
                        label = "Platforma",
                        selectedValue = selectedPlatform,
                        options = Platform.entries,
                        onValueChange = onPlatformChange
                    )

                    FilterDropdown(
                        label = "Kategorie",
                        selectedValue = selectedCategory,
                        options = Category.entries,
                        onValueChange = onCategoryChange
                    )

                    FilterDropdown(
                        label = "Řadit podle",
                        selectedValue = selectedSortBy,
                        options = SortBy.entries,
                        onValueChange = onSortByChange
                    )

                    FilterDropdown(
                        label = "Zobrazení",
                        selectedValue = selectedViewMode,
                        options = ViewMode.entries,
                        onValueChange = onViewModeChange
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onResetFilters,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Resetovat")
                        }

                        Button(
                            onClick = {
                                onApplyFilters()
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Použít")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> FilterDropdown(
    label: String,
    selectedValue: T,
    options: List<T>,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = when (selectedValue) {
                is Platform -> selectedValue.displayName
                is Category -> selectedValue.displayName
                is SortBy -> selectedValue.displayName
                is ViewMode -> selectedValue.displayName
                else -> selectedValue.toString()
            },
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            when (option) {
                                is Platform -> option.displayName
                                is Category -> option.displayName
                                is SortBy -> option.displayName
                                is ViewMode -> option.displayName
                                else -> option.toString()
                            }
                        )
                    },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
