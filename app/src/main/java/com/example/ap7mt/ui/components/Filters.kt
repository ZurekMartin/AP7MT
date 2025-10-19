package com.example.ap7mt.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExposedDropdownMenuDefaults
import com.example.ap7mt.R
import com.example.ap7mt.data.model.Platform
import com.example.ap7mt.data.model.Category
import com.example.ap7mt.data.model.SortBy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Filters(
    selectedPlatform: Platform,
    selectedCategory: Category,
    selectedSortBy: SortBy,
    onPlatformChange: (Platform) -> Unit,
    onCategoryChange: (Category) -> Unit,
    onSortByChange: (SortBy) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.filters),
            style = MaterialTheme.typography.titleMedium
        )

        FilterDropdown(
            label = stringResource(R.string.platform),
            selectedValue = selectedPlatform,
            options = Platform.entries,
            onValueChange = onPlatformChange
        )

        FilterDropdown(
            label = stringResource(R.string.category),
            selectedValue = selectedCategory,
            options = Category.entries,
            onValueChange = onCategoryChange
        )

        FilterDropdown(
            label = stringResource(R.string.sort_by),
            selectedValue = selectedSortBy,
            options = SortBy.entries,
            onValueChange = onSortByChange
        )
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
                else -> selectedValue.toString()
            },
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            when (option) {
                                is Platform -> option.displayName
                                is Category -> option.displayName
                                is SortBy -> option.displayName
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
