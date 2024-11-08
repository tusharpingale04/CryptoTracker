package com.tushar.presentation.ui.state

import androidx.compose.runtime.Immutable
import com.tushar.domain.models.CryptoCoin

@Immutable
data class Crypto(
    val name: String,
    val symbol: String,
    val isNew: Boolean,
    val isActive: Boolean,
    val type: String
) {
    companion object {
        fun from(cryptoCoin: CryptoCoin): Crypto {
            return Crypto(
                name = cryptoCoin.name,
                symbol = cryptoCoin.symbol,
                isNew = cryptoCoin.isNew,
                isActive = cryptoCoin.isActive,
                type = cryptoCoin.type
            )
        }
    }
}
