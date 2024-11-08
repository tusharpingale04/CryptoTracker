package com.tushar.domain.models

data class CryptoCoin (
    val name: String,
    val symbol: String,
    val isNew: Boolean,
    val isActive: Boolean,
    val type: String
)