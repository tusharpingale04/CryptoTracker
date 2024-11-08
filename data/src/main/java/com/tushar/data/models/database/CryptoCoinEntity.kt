package com.tushar.data.models.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crypto_coins_table")
data class CryptoCoinEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo("id")
    val id: String,
    @ColumnInfo("name")
    val name: String,
    @ColumnInfo("symbol")
    val symbol: String,
    @ColumnInfo("isNew")
    val isNew: Boolean,
    @ColumnInfo("isActive")
    val isActive: Boolean,
    @ColumnInfo("type")
    val type: String
)