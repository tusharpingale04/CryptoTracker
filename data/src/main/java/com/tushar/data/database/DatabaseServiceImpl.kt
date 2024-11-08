package com.tushar.data.database

import com.tushar.data.models.database.CryptoCoinEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseServiceImpl @Inject constructor(
    private val dao: CryptoCoinDao
) : DatabaseService {
    override fun searchCrypto(
        searchQuery: String?,
        isActive: Boolean?,
        isNotActive: Boolean?,
        coinType: String?,
        isNew: Boolean?
    ): Flow<List<CryptoCoinEntity>> {
        return dao.getAllCoins(
            searchQuery = searchQuery,
            isActive = isActive,
            isNotActive = isNotActive,
            coinType = coinType,
            isNew = isNew
        )
    }

    override suspend fun insertCryptos(cryptoCurrencies: List<CryptoCoinEntity>) {
        dao.insertAll(cryptoCurrencies)
    }

    override suspend fun hasCryptoEntries(): Boolean {
        return dao.hasCryptoEntries()
    }
}