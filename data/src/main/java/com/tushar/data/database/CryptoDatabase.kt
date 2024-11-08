package com.tushar.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tushar.data.mapper.Converters
import com.tushar.data.models.database.CryptoCoinEntity

@Database(entities = [CryptoCoinEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class CryptoDatabase : RoomDatabase() {
    abstract fun cryptoCoinDao(): CryptoCoinDao
}