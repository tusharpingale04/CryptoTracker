package com.tushar.data.mapper

import com.tushar.data.models.database.CryptoCoinEntity
import com.tushar.data.models.network.CryptoCoinDTO
import com.tushar.domain.models.CryptoCoin

fun CryptoCoinDTO.toEntity() = CryptoCoinEntity(
    id = "${this.name}_${this.symbol}",
    name = this.name,
    symbol = this.symbol,
    isNew = this.isNew,
    isActive = this.isActive,
    type = this.type
)

fun CryptoCoinEntity.toDomainModel() : CryptoCoin {
    val crypto = this
    return CryptoCoin(
        name = crypto.name,
        symbol = crypto.symbol,
        isNew = crypto.isNew,
        isActive = crypto.isActive,
        type = crypto.type
    )
}