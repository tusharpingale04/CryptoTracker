package com.tushar.data.network

import com.tushar.data.models.network.CryptoCoinDTO
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {
    @GET
    suspend fun getCryptoCoins(@Url url: String = ""): Result<List<CryptoCoinDTO>>
}