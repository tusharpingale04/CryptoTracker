package com.tushar.cryptotracker.ui.state

data class SearchParams private constructor(
    var query: String = "",
    var isActive: Boolean? = null,
    var isNotActive: Boolean? = null,
    var coinType: String? = null,
    var isNew: Boolean? = null
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SearchParams) return false

        return query == other.query &&
                isActive == other.isActive &&
                isNotActive == other.isNotActive &&
                coinType == other.coinType &&
                isNew == other.isNew
    }

    override fun hashCode(): Int {
        var result = query.hashCode()
        result = 31 * result + (isActive?.hashCode() ?: 0)
        result = 31 * result + (isNotActive?.hashCode() ?: 0)
        result = 31 * result + (coinType?.hashCode() ?: 0)
        result = 31 * result + (isNew?.hashCode() ?: 0)
        return result
    }

    companion object {
        val initialState
            get() = SearchParams()
    }
}
