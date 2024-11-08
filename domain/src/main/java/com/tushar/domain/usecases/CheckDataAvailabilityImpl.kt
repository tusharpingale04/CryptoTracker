package com.tushar.domain.usecases

import com.tushar.domain.repository.CryptoRepository
import javax.inject.Inject

class CheckDataAvailabilityImpl @Inject constructor(
    private val repository: CryptoRepository
): CheckDataAvailability {
    override suspend fun invoke(): Boolean {
        return repository.hasCryptoEntries()
    }
}