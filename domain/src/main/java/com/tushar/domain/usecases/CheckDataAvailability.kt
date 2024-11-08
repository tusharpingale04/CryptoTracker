package com.tushar.domain.usecases

interface CheckDataAvailability {
    suspend operator fun invoke(): Boolean
}