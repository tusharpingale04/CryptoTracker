package com.tushar.data.database

import com.tushar.data.models.database.CryptoCoinEntity
import kotlinx.coroutines.flow.Flow

interface DatabaseService {

    fun searchCrypto(
        searchQuery: String?,
        isActive: Boolean?,
        isNotActive: Boolean?,
        coinType: String?,
        isNew: Boolean?
    ): Flow<List<CryptoCoinEntity>>

    suspend fun insertCryptos(cryptoCurrencies: List<CryptoCoinEntity>)

    suspend fun hasCryptoEntries(): Boolean
}