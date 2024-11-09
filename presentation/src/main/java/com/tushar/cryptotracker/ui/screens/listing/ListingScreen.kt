package com.tushar.cryptotracker.ui.screens.listing

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tushar.cryptotracker.R
import com.tushar.cryptotracker.ui.state.Chip
import com.tushar.cryptotracker.ui.state.Crypto
import com.tushar.cryptotracker.ui.state.FilterType
import com.tushar.cryptotracker.ui.state.SearchWidgetState
import com.tushar.cryptotracker.ui.theme.Gray10
import com.tushar.cryptotracker.ui.theme.Gray60
import com.tushar.cryptotracker.ui.theme.Purple90
import kotlinx.collections.immutable.ImmutableList
import java.util.Locale

@Composable
fun ListingScreen() {
    val viewmodel = hiltViewModel<ListingViewModel>()
    val state by viewmodel.state.collectAsStateWithLifecycle()
    ListingContent(
        state = state,
        onTextChange = {
            viewmodel.updateSearchQuery(query = it)
        },
        onCloseClicked = {
            viewmodel.updateSearchWidgetState(state = SearchWidgetState.CLOSED)
        },
        onSearchTriggered = {
            viewmodel.updateSearchWidgetState(state = SearchWidgetState.OPENED)
        },
        onChipClick = { position, isSelected ->
            viewmodel.updateFilter(position = position, isSelected = isSelected)
        },
        retry = {
            viewmodel.fetchFromRemote(isFromRetry = true)
        }
    )
}

@Composable
fun ListingContent(
    state: ListingViewModel.ListingScreenState,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchTriggered: () -> Unit,
    onChipClick: (position: Int, isSelected: Boolean) -> Unit,
    retry: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            SearchAppBar(
                searchWidgetState = state.searchWidgetState,
                searchTextState = state.searchQuery,
                onTextChange = onTextChange,
                onCloseClicked = onCloseClicked,
                onSearchTriggered = onSearchTriggered
            )
        },
    ) { values ->
        val modifier = Modifier
            .fillMaxSize()
            .padding(values)
        if (state.isLoading) {
            LoadingScreen(modifier)
        } else if (state.isError) {
            ErrorScreen(modifier = modifier, retry = retry)
        } else {
            CryptoScreen(
                modifier = modifier,
                state = state,
                onChipClick = onChipClick
            )
        }
    }
}

@Composable
fun CryptoScreen(
    modifier: Modifier = Modifier,
    state: ListingViewModel.ListingScreenState,
    onChipClick: (position: Int, isSelected: Boolean) -> Unit
) {
    Column(
        modifier = modifier.testTag("cryptoScreen")
    ) {
        CryptoCurrencies(currencies = state.currencies)
        Filters(state = state, onChipClick = onChipClick)
    }
}

@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    retry: () -> Unit
) {
    Box(modifier = modifier.testTag("errorScreen"), contentAlignment = Alignment.Center) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Something went wrong! Please try again",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Button(onClick = {
                retry()
            }) {
                Text(text = "Retry")
            }
        }
    }
}

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.testTag("loadingScreen"), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun Filters(
    state: ListingViewModel.ListingScreenState,
    onChipClick: (position: Int, isSelected: Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                color = Color.LightGray,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomEnd = 0.dp,
                    bottomStart = 0.dp
                )
            )
            .padding(16.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.filters.size) { index ->
                val chip = state.filters.getOrNull(index)
                if (chip != null) {
                    ChipItem(chip = chip) { isSelected ->
                        onChipClick(index, isSelected)
                    }
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.CryptoCurrencies(
    currencies: ImmutableList<Crypto>
) {
    LazyColumn(
        modifier = Modifier.weight(1f).testTag("currenciesList")
    ) {
        items(currencies.size) { index ->
            val crypto = currencies.getOrNull(index)
            if (crypto != null) {
                CryptoItem(crypto = crypto)
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun ChipItem(chip: Chip, onChipClick: (isSelected: Boolean) -> Unit) {
    val backgroundColor = if (chip.isSelected) Gray60 else Gray10
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = { onChipClick(!chip.isSelected) })
            .background(color = backgroundColor)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (chip.isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Checked Icon",
                tint = Color.Black.copy(alpha = 0.8f),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }

        Text(
            text = chip.name,
            color = Color.Black.copy(alpha = 0.8f),
            modifier = Modifier
                .padding(vertical = 8.dp),
            style = TextStyle(fontWeight = if (chip.isSelected) FontWeight.Medium else FontWeight.Normal),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAppBar(
    searchWidgetState: SearchWidgetState,
    searchTextState: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchTriggered: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(searchWidgetState) {
        if (searchWidgetState == SearchWidgetState.OPENED) {
            focusRequester.requestFocus()
        }
    }
    TopAppBar(
        modifier = Modifier.testTag("searchBar"),
        title = {
            if (searchWidgetState == SearchWidgetState.OPENED) {
                TextField(
                    modifier = Modifier
                        .testTag("searchTextField")
                        .focusRequester(focusRequester)
                        .fillMaxWidth(),
                    value = searchTextState,
                    onValueChange = {
                        onTextChange(it)
                    },
                    placeholder = {
                        Text(
                            modifier = Modifier
                                .alpha(alpha = 0.6f),
                            text = "Search coin name or symbol",
                            color = Color.White
                        )
                    },
                    textStyle = TextStyle(
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        color = Color.White.copy(alpha = 0.8f)
                    ),
                    singleLine = true,
                    leadingIcon = {
                        IconButton(
                            modifier = Modifier,
                            onClick = {}
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon",
                                tint = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.1f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                        cursorColor = Color.White
                    )
                )
            } else {
                Text(text = "Crypto Tracker", color = Color.White)
            }
        },
        actions = {
            IconButton(onClick = {
                if (searchWidgetState == SearchWidgetState.OPENED) {
                    onTextChange("")
                    onCloseClicked()
                } else {
                    onSearchTriggered()
                }
            }) {
                if (searchWidgetState == SearchWidgetState.OPENED) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Icon",
                        tint = Color.White.copy(alpha = 0.8f)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.White.copy(alpha = 0.8f),
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Purple90,
            titleContentColor = Color.White
        )
    )

}

@Composable
fun CryptoItem(
    crypto: Crypto
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .background(color = Color.White)
    ) {

        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            Text(
                text = crypto.name,
                modifier = Modifier.padding(start = 16.dp),
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal
                )
            )

            Text(
                text = crypto.symbol.uppercase(Locale.ENGLISH),
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        if (crypto.isNew) {
            Image(
                painter = painterResource(R.drawable.ic_new_tag),
                contentDescription = "New Tag",
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.TopEnd)
            )
        }

        Image(
            painter = if (!crypto.isActive) {
                painterResource(R.drawable.ic_coin_inactive)
            } else if (crypto.type == "coin") {
                painterResource(R.drawable.ic_coin)
            } else {
                painterResource(R.drawable.ic_token)
            },
            contentDescription = "New Tag",
            modifier = Modifier
                .padding(end = 16.dp)
                .size(48.dp)
                .align(Alignment.CenterEnd)
        )


    }
}

@Composable
@Preview
fun CryptoItemPreview() {
    CryptoItem(
        Crypto(
            name = "Bitcoin",
            symbol = "BTC",
            isNew = true,
            isActive = true,
            type = "coin"
        )
    )
}

@Composable
@Preview
fun SearchAppBarOpenedPreview() {
    SearchAppBar(
        searchWidgetState = SearchWidgetState.OPENED,
        onTextChange = {},
        onCloseClicked = {},
        searchTextState = "Bitcoin",
        onSearchTriggered = {}
    )
}

@Composable
@Preview
fun SearchAppBarOpenedWithoutQueryPreview() {
    SearchAppBar(
        searchWidgetState = SearchWidgetState.OPENED,
        onTextChange = {},
        onCloseClicked = {},
        searchTextState = "",
        onSearchTriggered = {}
    )
}

@Composable
@Preview
fun SearchAppBarClosedPreview() {
    SearchAppBar(
        searchWidgetState = SearchWidgetState.CLOSED,
        onTextChange = {},
        onCloseClicked = {},
        searchTextState = "Bitcoin",
        onSearchTriggered = {}
    )
}

@Composable
@Preview
fun ChipSelectedPreview() {
    ChipItem(
        chip = Chip(
            name = "Active Coin",
            isSelected = true,
            type = FilterType.ACTIVE_COINS
        )
    ) {}
}

@Composable
@Preview
fun ChipDeSelectedPreview() {
    ChipItem(
        chip = Chip(
            name = "InActive Coin",
            isSelected = false,
            type = FilterType.ACTIVE_COINS
        )
    ) {}
}