package com.tushar.cryptotracker.di

import com.tushar.data.database.DatabaseService
import com.tushar.data.database.DatabaseServiceImpl
import com.tushar.data.repository.CryptoRepositoryImpl
import com.tushar.domain.repository.CryptoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {

    @Binds
    @Singleton
    fun provideDatabaseService(service: DatabaseServiceImpl): DatabaseService

    @Binds
    @Singleton
    fun provideCryptoRepository(repository: CryptoRepositoryImpl): CryptoRepository

}