package com.tushar.cryptotracker.ui.screens.listing

import com.tushar.cryptotracker.ui.state.SearchWidgetState
import com.tushar.domain.usecases.CheckDataAvailability
import com.tushar.domain.usecases.GetCryptos
import com.tushar.domain.usecases.SearchAndFilterCryptos
import com.tushar.cryptotracker.utils.MainDispatcherRule
import com.tushar.domain.models.CryptoCoin
import com.tushar.domain.models.ResultState
import io.mockk.clearAllMocks
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class ListingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var SUT: ListingViewModel
    private val getCryptos = mockk<GetCryptos>(relaxed = true)
    private val searchAndFilterCryptos = mockk<SearchAndFilterCryptos>(relaxed = true)
    private val checkDataAvailability = mockk<CheckDataAvailability>(relaxed = true)

    @Before
    fun setup() {

    }

    @Test
    fun `if data is present on local device, remove loader and show data`() = runTest {
        //Assign

        SUT = ListingViewModel(
            getCryptos = getCryptos,
            searchAndFilter = searchAndFilterCryptos,
            checkDataAvailability = checkDataAvailability,
            defaultDispatcher = mainDispatcherRule.testDispatcher,
            ioDispatcher = mainDispatcherRule.testDispatcher
        )

        coEvery { checkDataAvailability() } returns true
        coEvery { getCryptos() } returns emptyFlow()
        coEvery { searchAndFilterCryptos.invoke(any(), any(), any(), any(), any()) } returns flow {
            emit(
                listOf(
                    CryptoCoin(
                        name = "Bitcoin",
                        symbol = "BTC",
                        isNew = false,
                        isActive = true,
                        type = "coin"
                    )
                )
            )
        }

        //Act
        SUT.listenToScreenState()
        advanceUntilIdle()

        //Assert
        assert(!SUT.state.value.isLoading)
        assert(SUT.state.value.currencies.size == 1)
        assert(SUT.state.value.currencies[0].name == "Bitcoin")
    }

    @Test
    fun `if data is not present on local device and api error out, show error screen`() = runTest {
        //Assign
        SUT = ListingViewModel(
            getCryptos = getCryptos,
            searchAndFilter = searchAndFilterCryptos,
            checkDataAvailability = checkDataAvailability,
            defaultDispatcher = mainDispatcherRule.testDispatcher,
            ioDispatcher = mainDispatcherRule.testDispatcher
        )

        coEvery { checkDataAvailability() } returns false
        coEvery { getCryptos() } returns flow {
            emit(ResultState.Error(message = "Something went wrong"))
        }
        coEvery { searchAndFilterCryptos.invoke(any(), any(), any(), any(), any()) } returns emptyFlow()

        //Act
        SUT.listenToScreenState()
        advanceUntilIdle()

        //Assert
        assert(SUT.state.value.isError)
    }

    @Test
    fun `test if data is not present on local device, wait for api response and show data to user`() = runTest {
        //Assign
        SUT = ListingViewModel(
            getCryptos = getCryptos,
            searchAndFilter = searchAndFilterCryptos,
            checkDataAvailability = checkDataAvailability,
            defaultDispatcher = mainDispatcherRule.testDispatcher,
            ioDispatcher = mainDispatcherRule.testDispatcher
        )

        coEvery { checkDataAvailability() } returns false
        coEvery { getCryptos() } returns flow {
            emit(ResultState.Success(Unit))
        }
        coEvery { searchAndFilterCryptos.invoke(any(), any(), any(), any(), any()) } returns flow {
            emit(
                listOf(
                    CryptoCoin(
                        name = "Bitcoin",
                        symbol = "BTC",
                        isNew = false,
                        isActive = true,
                        type = "coin"
                    )
                )
            )
        }

        //Act
        SUT.listenToScreenState()
        advanceUntilIdle()

        //Assert
        assert(SUT.state.value.currencies.size == 1)
        assert(SUT.state.value.currencies[0].name == "Bitcoin")
    }

    @Test
    fun `test if retry is working fine, if screen error out and retry is clicked`() = runTest {
        //Assign
        SUT = ListingViewModel(
            getCryptos = getCryptos,
            searchAndFilter = searchAndFilterCryptos,
            checkDataAvailability = checkDataAvailability,
            defaultDispatcher = mainDispatcherRule.testDispatcher,
            ioDispatcher = mainDispatcherRule.testDispatcher
        )

        coEvery { checkDataAvailability() } returns false
        coEvery { getCryptos() } returns flow {
            emit(ResultState.Error(message = "Something went wrong"))
        }
        coEvery { searchAndFilterCryptos.invoke(any(), any(), any(), any(), any()) } returns emptyFlow()

        SUT.listenToScreenState()
        advanceUntilIdle()

        assert(SUT.state.value.isError)

        clearMocks(searchAndFilterCryptos)
        clearMocks(getCryptos)
        coEvery { getCryptos() } returns flow {
            emit(ResultState.Success(Unit))
        }
        coEvery { searchAndFilterCryptos.invoke(any(), any(), any(), any(), any()) } returns flow {
            emit(
                listOf(
                    CryptoCoin(
                        name = "Bitcoin",
                        symbol = "BTC",
                        isNew = false,
                        isActive = true,
                        type = "coin"
                    )
                )
            )
        }

        //Act
        SUT.fetchFromRemote(isFromRetry = true)
        SUT.updateSearchQuery("bit")
        advanceUntilIdle()

        //Assert
        assert(SUT.state.value.currencies.size == 1)
        assert(SUT.state.value.currencies[0].name == "Bitcoin")
    }

    @Test
    fun `test if search bar is opened`() = runTest {
        //Assign
        SUT = ListingViewModel(
            getCryptos = getCryptos,
            searchAndFilter = searchAndFilterCryptos,
            checkDataAvailability = checkDataAvailability,
            defaultDispatcher = mainDispatcherRule.testDispatcher,
            ioDispatcher = mainDispatcherRule.testDispatcher
        )

        //Act
        SUT.updateSearchWidgetState(SearchWidgetState.OPENED)
        advanceUntilIdle()

        //Assert
        assert(SUT.state.value.searchWidgetState == SearchWidgetState.OPENED)
    }

    @Test
    fun `test if search bar is closed`() = runTest {
        //Assign
        SUT = ListingViewModel(
            getCryptos = getCryptos,
            searchAndFilter = searchAndFilterCryptos,
            checkDataAvailability = checkDataAvailability,
            defaultDispatcher = mainDispatcherRule.testDispatcher,
            ioDispatcher = mainDispatcherRule.testDispatcher
        )

        //Act
        SUT.updateSearchWidgetState(SearchWidgetState.CLOSED)
        advanceUntilIdle()

        //Assert
        assert(SUT.state.value.searchWidgetState == SearchWidgetState.CLOSED)
    }

    @Test
    fun `test if search query is updated`() = runTest {
        //Assign
        SUT = ListingViewModel(
            getCryptos = getCryptos,
            searchAndFilter = searchAndFilterCryptos,
            checkDataAvailability = checkDataAvailability,
            defaultDispatcher = mainDispatcherRule.testDispatcher,
            ioDispatcher = mainDispatcherRule.testDispatcher
        )

        //Act
        SUT.updateSearchQuery("bit")
        advanceUntilIdle()

        //Assert
        assert(SUT.state.value.searchQuery == "bit")
    }

    @Test
    fun `test if filter data is populated`() = runTest {
        //Assign
        SUT = ListingViewModel(
            getCryptos = getCryptos,
            searchAndFilter = searchAndFilterCryptos,
            checkDataAvailability = checkDataAvailability,
            defaultDispatcher = mainDispatcherRule.testDispatcher,
            ioDispatcher = mainDispatcherRule.testDispatcher
        )

        //Act
        SUT.populateFilterData()
        advanceUntilIdle()

        //Assert
        assert(SUT.state.value.filters.size == 5)
    }

    @Test
    fun `test if filter chip is selected on click`() = runTest {
        //Assign
        SUT = ListingViewModel(
            getCryptos = getCryptos,
            searchAndFilter = searchAndFilterCryptos,
            checkDataAvailability = checkDataAvailability,
            defaultDispatcher = mainDispatcherRule.testDispatcher,
            ioDispatcher = mainDispatcherRule.testDispatcher
        )

        //Act
        SUT.populateFilterData()
        advanceUntilIdle()

        SUT.updateFilter(0, true)
        advanceUntilIdle()

        //Assert
        assert(SUT.state.value.filters.size == 5)
        assert(SUT.state.value.filters[0].isSelected)
    }

    @After
    fun after() {
        clearAllMocks()
    }

}