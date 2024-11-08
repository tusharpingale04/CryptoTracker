package com.tushar.domain.repository

import com.tushar.domain.models.CryptoCoin
import com.tushar.domain.models.ResultState
import kotlinx.coroutines.flow.Flow

interface CryptoRepository {

    suspend fun searchCrypto(
        searchQuery: String?,
        isActive: Boolean?,
        isNotActive: Boolean?,
        coinType: String?,
        isNew: Boolean?
    ): Flow<List<CryptoCoin>>

    fun fetchAllCryptoCurrencies(): Flow<ResultState<Unit>>

    suspend fun hasCryptoEntries(): Boolean
}