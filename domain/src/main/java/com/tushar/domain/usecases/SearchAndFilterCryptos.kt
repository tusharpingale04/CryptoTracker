package com.tushar.domain.usecases

import com.tushar.domain.models.CryptoCoin
import kotlinx.coroutines.flow.Flow

interface SearchAndFilterCryptos {
    suspend operator fun invoke(
        searchQuery: String?,
        isActive: Boolean?,
        isNotActive: Boolean?,
        coinType: String?,
        isNew: Boolean?
    ): Flow<List<CryptoCoin>>
}