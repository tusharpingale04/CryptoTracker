package com.tushar.cryptotracker.di

import com.tushar.domain.usecases.CheckDataAvailability
import com.tushar.domain.usecases.CheckDataAvailabilityImpl
import com.tushar.domain.usecases.GetCryptos
import com.tushar.domain.usecases.GetCryptosImpl
import com.tushar.domain.usecases.SearchAndFilterCryptos
import com.tushar.domain.usecases.SearchAndFilterCryptosImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface UseCaseModule {

    @Binds
    @Singleton
    fun provideGetCryptoUseCase(getCryptos: GetCryptosImpl): GetCryptos

    @Binds
    @Singleton
    fun provideSearchAndFilterUseCase(searchAndFilterCryptos: SearchAndFilterCryptosImpl): SearchAndFilterCryptos

    @Binds
    @Singleton
    fun provideCheckDataAvailabilityUseCase(checkDataAvailability: CheckDataAvailabilityImpl): CheckDataAvailability

}