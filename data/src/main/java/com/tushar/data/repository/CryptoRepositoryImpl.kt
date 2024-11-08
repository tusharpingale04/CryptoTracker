package com.tushar.data.repository

import android.util.Log
import com.tushar.data.database.DatabaseService
import com.tushar.data.mapper.toDomainModel
import com.tushar.data.mapper.toEntity
import com.tushar.data.models.network.CryptoCoinDTO
import com.tushar.data.network.ApiService
import com.tushar.domain.models.CryptoCoin
import com.tushar.domain.models.ResultState
import com.tushar.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoRepositoryImpl @Inject constructor(
    private val networkService: ApiService,
    private val databaseService: DatabaseService
): CryptoRepository {

    override suspend fun searchCrypto(
        searchQuery: String?,
        isActive: Boolean?,
        isNotActive: Boolean?,
        coinType: String?,
        isNew: Boolean?
    ): Flow<List<CryptoCoin>> {
        return databaseService.searchCrypto(
            searchQuery = searchQuery,
            isActive = isActive,
            isNotActive = isNotActive,
            coinType = coinType,
            isNew = isNew
        ).map { currencies ->
            currencies.map { it.toDomainModel() }
        }
    }

    override fun fetchAllCryptoCurrencies(): Flow<ResultState<Unit>> {
        return flow {
            emit(ResultState.Loading)
            networkService.getCryptoCoins().onSuccess {
                emit(ResultState.Success(Unit))
                addCryptoCurrenciesInDB(it)
            }.onFailure {
                emit(ResultState.Error(message = it.message ?: "Something went wrong"))
                Log.e("CryptoRepository", "error: ${it.message}")
            }
        }
    }

    override suspend fun hasCryptoEntries(): Boolean {
        return databaseService.hasCryptoEntries()
    }

    private suspend fun addCryptoCurrenciesInDB(cryptoCurrencies: List<CryptoCoinDTO>) {
        val currencyEntities = cryptoCurrencies.map {
            it.toEntity()
        }
        databaseService.insertCryptos(currencyEntities)
    }

}