package com.tushar.presentation.ui.screens.listing

import com.tushar.domain.usecases.CheckDataAvailability
import com.tushar.domain.usecases.GetCryptos
import com.tushar.domain.usecases.SearchAndFilterCryptos
import com.tushar.presentation.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ListingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var SUT: ListingViewModel
    private val getCryptos = mockk<GetCryptos>()
    private val searchAndFilterCryptos = mockk<SearchAndFilterCryptos>()
    private val getAvailability = mockk<CheckDataAvailability>()

    @Before
    fun setup() {
        SUT = ListingViewModel(
            getCryptos = getCryptos,
            searchAndFilter = searchAndFilterCryptos,
            checkDataAvailability = getAvailability,
            defaultDispatcher = mainDispatcherRule.testDispatcher,
            ioDispatcher = mainDispatcherRule.testDispatcher
        )
    }

    @Test
    fun `if data is present on local device, remove loader`() {
        //Assign
        coEvery { getAvailability() } returns true

        //Act

        //Assert
        assert(!SUT.state.value.isLoading)
    }

    @After
    fun after() {
        unmockkAll()
    }

}