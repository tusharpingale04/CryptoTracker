package com.tushar.domain.usecases

import com.tushar.domain.models.ResultState
import com.tushar.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCryptosImpl @Inject constructor(
    private val repository: CryptoRepository
): GetCryptos {
    override suspend fun invoke(): Flow<ResultState<Unit>> {
        return repository.fetchAllCryptoCurrencies()
    }
}