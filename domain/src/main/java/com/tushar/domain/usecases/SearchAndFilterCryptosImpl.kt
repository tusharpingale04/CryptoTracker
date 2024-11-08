package com.tushar.domain.usecases

import com.tushar.domain.models.CryptoCoin
import com.tushar.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchAndFilterCryptosImpl @Inject constructor(
    private val repository: CryptoRepository
) : SearchAndFilterCryptos {
    override suspend fun invoke(
        searchQuery: String?,
        isActive: Boolean?,
        isNotActive: Boolean?,
        coinType: String?,
        isNew: Boolean?
    ): Flow<List<CryptoCoin>> {
        return repository.searchCrypto(
            searchQuery = searchQuery,
            isActive = isActive,
            isNotActive = isNotActive,
            coinType = coinType,
            isNew = isNew
        )
    }
}