package com.tushar.cryptotracker.di

import android.content.Context
import androidx.room.Room
import com.tushar.data.database.CryptoCoinDao
import com.tushar.data.database.CryptoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): CryptoDatabase {
        return Room.databaseBuilder(context, CryptoDatabase::class.java, "crypto_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideCryptoCoinDao(db: CryptoDatabase): CryptoCoinDao {
        return db.cryptoCoinDao()
    }
}