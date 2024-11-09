package com.tushar.cryptotracker

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tushar.cryptotracker.ui.screens.listing.ListingContent
import com.tushar.cryptotracker.ui.screens.listing.ListingViewModel
import com.tushar.cryptotracker.ui.state.Crypto
import com.tushar.cryptotracker.ui.state.SearchWidgetState
import com.tushar.cryptotracker.ui.theme.CryptoTrackerTheme
import kotlinx.collections.immutable.persistentListOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ListingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test_if_loader_screen_is_shown() {
        composeTestRule.setContent {
            CryptoTrackerTheme {
                ListingContent(
                    state = ListingViewModel.ListingScreenState.initialState.copy(
                        isLoading = true
                    ),
                    onTextChange = {},
                    onCloseClicked = {},
                    onSearchTriggered = {},
                    onChipClick = { _, _ -> },
                    retry = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("errorScreen").assertIsNotDisplayed()
        composeTestRule.onNodeWithTag("loadingScreen").assertIsDisplayed()
    }

    @Test
    fun test_if_error_screen_is_shown() {
        composeTestRule.setContent {
            CryptoTrackerTheme {
                ListingContent(
                    state = ListingViewModel.ListingScreenState.initialState.copy(
                        isLoading = false,
                        isError = true
                    ),
                    onTextChange = {},
                    onCloseClicked = {},
                    onSearchTriggered = {},
                    onChipClick = { _, _ -> },
                    retry = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("loadingScreen").assertIsNotDisplayed()
        composeTestRule.onNodeWithTag("errorScreen").assertIsDisplayed()
    }

    @Test
    fun test_if_crypto_screen_is_shown() {
        composeTestRule.setContent {
            CryptoTrackerTheme {
                ListingContent(
                    state = ListingViewModel.ListingScreenState.initialState.copy(
                        isLoading = false,
                        isError = false,
                        currencies = persistentListOf(
                            Crypto(
                                name = "Bitcoin",
                                symbol = "BTC",
                                isNew = false,
                                isActive = true,
                                type = "coin"
                            )
                        )
                    ),
                    onTextChange = {},
                    onCloseClicked = {},
                    onSearchTriggered = {},
                    onChipClick = { _, _ -> },
                    retry = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("loadingScreen").assertIsNotDisplayed()
        composeTestRule.onNodeWithTag("errorScreen").assertIsNotDisplayed()
        composeTestRule.onNodeWithTag("cryptoScreen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("currenciesList")
            .performScrollToNode(hasText("Bitcoin"))
            .assertIsDisplayed()
    }

    @Test
    fun test_if_search_bar_is_shown() {
        composeTestRule.setContent {
            CryptoTrackerTheme {
                ListingContent(
                    state = ListingViewModel.ListingScreenState.initialState.copy(
                        isLoading = false,
                        isError = false,
                        currencies = persistentListOf(
                            Crypto(
                                name = "Bitcoin",
                                symbol = "BTC",
                                isNew = false,
                                isActive = true,
                                type = "coin"
                            )
                        ),
                        searchWidgetState = SearchWidgetState.OPENED
                    ),
                    onTextChange = {},
                    onCloseClicked = {},
                    onSearchTriggered = {},
                    onChipClick = { _, _ -> },
                    retry = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("searchBar").assertIsDisplayed()
    }

    @Test
    fun test_if_query_is_shown_in_search_bar() {
        composeTestRule.setContent {
            CryptoTrackerTheme {
                ListingContent(
                    state = ListingViewModel.ListingScreenState.initialState.copy(
                        isLoading = false,
                        isError = false,
                        currencies = persistentListOf(
                            Crypto(
                                name = "Bitcoin",
                                symbol = "BTC",
                                isNew = false,
                                isActive = true,
                                type = "coin"
                            )
                        ),
                        searchWidgetState = SearchWidgetState.OPENED,
                        searchQuery = "Bitcoin"
                    ),
                    onTextChange = {},
                    onCloseClicked = {},
                    onSearchTriggered = {},
                    onChipClick = { _, _ -> },
                    retry = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("searchBar").assertIsDisplayed()
        composeTestRule.onNodeWithTag("searchTextField").assertIsDisplayed().assert(hasText("Bitcoin"))
    }


}