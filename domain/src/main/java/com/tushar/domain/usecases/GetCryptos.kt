package com.tushar.domain.usecases

import com.tushar.domain.models.ResultState
import kotlinx.coroutines.flow.Flow

interface GetCryptos {
    suspend operator fun invoke(): Flow<ResultState<Unit>>
}