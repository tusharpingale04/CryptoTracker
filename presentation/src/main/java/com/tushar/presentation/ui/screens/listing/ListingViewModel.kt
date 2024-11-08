package com.tushar.presentation.ui.screens.listing

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tushar.domain.models.ResultState
import com.tushar.domain.usecases.CheckDataAvailability
import com.tushar.domain.usecases.GetCryptos
import com.tushar.domain.usecases.SearchAndFilterCryptos
import com.tushar.presentation.di.DefaultDispatcher
import com.tushar.presentation.di.IoDispatcher
import com.tushar.presentation.ui.state.Chip
import com.tushar.presentation.ui.state.Crypto
import com.tushar.presentation.ui.state.FilterType
import com.tushar.presentation.ui.state.SearchParams
import com.tushar.presentation.ui.state.SearchWidgetState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListingViewModel @Inject constructor(
    private val getCryptos: GetCryptos,
    private val searchAndFilter: SearchAndFilterCryptos,
    private val checkDataAvailability: CheckDataAvailability,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {

    private val _state = MutableStateFlow(ListingScreenState.initialState)
    val state: StateFlow<ListingScreenState> = _state

    private val _searchParams = MutableStateFlow(SearchParams.initialState)

    private val isErrorFromNetwork = CompletableDeferred<Boolean>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: Flow<List<Crypto>> = _searchParams
        .flatMapLatest { params ->
            searchAndFilter(
                searchQuery = params.query,
                isActive = params.isActive,
                isNotActive = params.isNotActive,
                coinType = params.coinType,
                isNew = params.isNew
            )
        }.map { cryptoCoins ->
            cryptoCoins.map { cryptoCoin ->
                Crypto.from(cryptoCoin)
            }
        }

    init {
        listenToScreenState()
        fetchFromRemote()
        subscribeToLocal()
        populateFilterData()
    }

    private fun listenToScreenState() {
        viewModelScope.launch(ioDispatcher) {
            val isDataPresentOnLocal = checkDataAvailability()
            _state.update { it.copy(isLoading = !isDataPresentOnLocal) }
            val isNetworkError = isErrorFromNetwork.await()
            if (!isDataPresentOnLocal && isNetworkError) {
                _state.update {
                    it.copy(isError = true, isLoading = false)
                }
            }
        }
    }

    private fun subscribeToLocal() {
        viewModelScope.launch(ioDispatcher) {
            searchResults.collect { currencies ->
                _state.update {
                    it.copy(
                        currencies = currencies.toImmutableList()
                    )
                }
            }
        }
    }

    fun fetchFromRemote(isFromRetry: Boolean = false) {
        viewModelScope.launch(ioDispatcher) {
            if (isFromRetry) {
                _state.update { it.copy(isLoading = true) }
            }
            getCryptos().collect { result ->
                when (result) {
                    is ResultState.Error -> {
                        completeLoadingFromNetwork(isError = true)
                    }
                    is ResultState.Success -> {
                        completeLoadingFromNetwork(isError = false)
                        if (isFromRetry) {
                            _state.update { it.copy(isError = false) }
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun completeLoadingFromNetwork(isError: Boolean) {
        _state.update { it.copy(isLoading = false) }
        if (!isErrorFromNetwork.isCompleted)
            isErrorFromNetwork.complete(isError)
    }

    fun updateSearchWidgetState(state: SearchWidgetState) {
        _state.update {
            it.copy(searchWidgetState = state)
        }
    }

    fun updateSearchQuery(query: String) {
        _state.update {
            it.copy(searchQuery = query)
        }
        updateSearchParams {
            this.query = query
        }
    }

    fun updateFilter(position: Int, isSelected: Boolean) {
        viewModelScope.launch(defaultDispatcher) {
            val currentFilters = state.value.filters.toMutableList()
            val chip = currentFilters.getOrNull(position)
            if (chip != null) {
                val updatedChip = chip.copy(isSelected = isSelected)
                currentFilters[position] = updatedChip
                _state.update {
                    it.copy(filters = currentFilters.toImmutableList())
                }
                updateFilterQuery(updatedChip, isSelected)
            }
        }
    }

    private fun updateFilterQuery(chip: Chip, isSelected: Boolean) {
        updateSearchParams {
            when (chip.type) {
                FilterType.ACTIVE_COINS -> if (isSelected) this.isActive = true else this.isActive = null
                FilterType.INACTIVE_COINS -> if (isSelected) this.isNotActive = true else this.isNotActive = null
                FilterType.ONLY_TOKENS -> if (isSelected) this.coinType = "token" else this.coinType = null
                FilterType.ONLY_COINS -> if (isSelected) this.coinType = "coin" else this.coinType = null
                FilterType.NEW_COINS -> if (isSelected) this.isNew = true else this.isNew = null
            }
        }
    }

    private fun populateFilterData() {
        viewModelScope.launch (defaultDispatcher) {
            val filters = persistentListOf(
                Chip(name = "Active Coins", isSelected = false, type = FilterType.ACTIVE_COINS),
                Chip(name = "InActive Coins", isSelected = false, type = FilterType.INACTIVE_COINS),
                Chip(name = "Only Tokens", isSelected = false, type = FilterType.ONLY_TOKENS),
                Chip(name = "Only Coins", isSelected = false, type = FilterType.ONLY_COINS),
                Chip(name = "New Coins", isSelected = false, type = FilterType.NEW_COINS)
            )
            _state.update {
                it.copy(filters = filters)
            }
        }
    }

    private fun updateSearchParams(update: SearchParams.() -> Unit) {
        _searchParams.value = _searchParams.value.copy().apply(update)
    }

    @Immutable
    data class ListingScreenState private constructor(
        val searchWidgetState: SearchWidgetState = SearchWidgetState.CLOSED,
        val searchQuery: String = "",
        val filters: ImmutableList<Chip> = persistentListOf(),
        val currencies: ImmutableList<Crypto> = persistentListOf(),
        val isError: Boolean = false,
        val isLoading: Boolean = true
    ) {
        companion object {
            val initialState
                get() = ListingScreenState()
        }
    }

}