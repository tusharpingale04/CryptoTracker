package com.tushar.cryptotracker.ui.state

import androidx.compose.runtime.Immutable

@Immutable
data class Chip(
    val name: String,
    val isSelected: Boolean,
    val type: FilterType
)
