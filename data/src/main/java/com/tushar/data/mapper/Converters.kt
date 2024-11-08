package com.tushar.data.mapper

import androidx.room.TypeConverter
import com.tushar.data.models.database.CryptoType

class Converters {

    @TypeConverter
    fun fromCryptoType(type: CryptoType): String {
        return type.name.lowercase()
    }

    @TypeConverter
    fun toCryptoType(type: String): CryptoType {
        return when (type.lowercase()) {
            "coin" -> CryptoType.COIN
            "token" -> CryptoType.TOKEN
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }

}