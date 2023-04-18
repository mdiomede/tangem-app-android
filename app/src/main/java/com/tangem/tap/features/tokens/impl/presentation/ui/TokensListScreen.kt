package com.tangem.tap.features.tokens.impl.presentation.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.tangem.core.ui.components.PrimaryButton
import com.tangem.core.ui.res.TangemColorPalette
import com.tangem.core.ui.res.TangemTheme
import com.tangem.tap.features.tokens.impl.presentation.states.TokenItemState
import com.tangem.tap.features.tokens.impl.presentation.states.TokensListStateHolder
import com.tangem.tap.features.tokens.impl.presentation.states.TokensListToolbarState
import com.tangem.wallet.R
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow

/**
 * @author Andrew Khokhlov on 27/03/2023
 */
@Composable
internal fun TokensListScreen(stateHolder: TokensListStateHolder) {
    BackHandler(onBack = stateHolder.toolbarState.onBackButtonClick)

    Scaffold(
        topBar = { TokensListToolbar(state = stateHolder.toolbarState) },
        floatingActionButton = {
            if (stateHolder is TokensListStateHolder.ManageContent) {
                SaveChangesButton(onClick = stateHolder.onSaveButtonClick)
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { scaffoldPadding ->
        val tokens = stateHolder.tokens.collectAsLazyPagingItems()

        TokensListContent(stateHolder.isDifferentAddressesBlockVisible, tokens, scaffoldPadding)

        stateHolder.onTokensLoadStateChanged(tokens.loadState.refresh)

        AnimatedVisibility(
            visible = stateHolder.isLoading,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            LoadingContent()
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = TangemColorPalette.White),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = TangemColorPalette.Meadow)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TokensListContent(
    isDifferentAddressesBlockVisible: Boolean,
    tokens: LazyPagingItems<TokenItemState>,
    scaffoldPadding: PaddingValues,
) {
    val state = rememberLazyListState()

    if (state.isScrollInProgress) {
        LocalSoftwareKeyboardController.current?.hide()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(scaffoldPadding),
        state = state,
    ) {
        if (isDifferentAddressesBlockVisible) {
            item { DifferentAddressesWarning() }
        }

        items(items = tokens, key = TokenItemState::name) {
            it?.let { TokenItem(model = it) }
        }
    }
}

@OptIn(ExperimentalUnitApi::class)
@Composable
private fun DifferentAddressesWarning() {
    Box(
        modifier = Modifier
            .padding(TangemTheme.dimens.spacing16)
            .background(
                color = TangemColorPalette.Light1,
                shape = RoundedCornerShape(TangemTheme.dimens.radius10),
            ),
        contentAlignment = Alignment.Center,
    ) {
        val text = stringResource(id = R.string.alert_manage_tokens_addresses_message)
        val firstSpaceIndex = text.indexOf(" ")
        Text(
            text = AnnotatedString(
                text = text,
                spanStyles = listOf(
                    AnnotatedString.Range(
                        SpanStyle(fontWeight = FontWeight.Bold),
                        start = 0,
                        end = firstSpaceIndex,
                    ),
                ),
            ),
            modifier = Modifier.padding(
                horizontal = TangemTheme.dimens.spacing16,
                vertical = TangemTheme.dimens.spacing8,
            ),
            color = TangemColorPalette.Dark1,
            textAlign = TextAlign.Start,
            style = TangemTheme.typography.body2.copy(
                letterSpacing = TextUnit(value = 0.5f, type = TextUnitType.Sp),
                lineHeight = TextUnit(value = 25f, type = TextUnitType.Sp),
            ),
        )
    }
}

@Composable
private fun SaveChangesButton(onClick: () -> Unit) {
    PrimaryButton(
        modifier = Modifier
            .imePadding()
            .padding(horizontal = TangemTheme.dimens.spacing16)
            .fillMaxWidth(),
        text = stringResource(id = R.string.common_save_changes),
        onClick = onClick,
    )
}

@Preview
@Composable
private fun Preview_TokensListScreen_Loading() {
    TangemTheme {
        TokensListScreen(
            stateHolder = TokensListStateHolder.ReadContent(
                toolbarState = TokensListToolbarState.Title.Manage(
                    titleResId = R.string.main_manage_tokens,
                    onBackButtonClick = {},
                    onSearchButtonClick = {},
                    onAddCustomTokenClick = {},
                ),
                isLoading = true,
                isDifferentAddressesBlockVisible = false,
                tokens = emptyFlow(),
                onTokensLoadStateChanged = {},
            ),
        )
    }
}

@Preview
@Composable
private fun Preview_TokensListScreen_Manage() {
    TangemTheme {
        TokensListScreen(
            stateHolder = TokensListStateHolder.ManageContent(
                toolbarState = TokensListToolbarState.Title.Manage(
                    titleResId = R.string.main_manage_tokens,
                    onBackButtonClick = {},
                    onSearchButtonClick = {},
                    onAddCustomTokenClick = {},
                ),
                isLoading = false,
                isDifferentAddressesBlockVisible = true,
                tokens = flow {
                    emit(
                        PagingData.from(
                            listOf(
                                TokenListPreviewData.createManageToken(),
                                TokenListPreviewData.createManageToken(),
                            ),
                        ),
                    )
                },
                onSaveButtonClick = {},
                onTokensLoadStateChanged = {},
            ),
        )
    }
}

@Preview
@Composable
private fun Preview_TokensListScreen_Read() {
    TangemTheme {
        TokensListScreen(
            stateHolder = TokensListStateHolder.ReadContent(
                toolbarState = TokensListToolbarState.Title.Read(
                    titleResId = R.string.search_tokens_title,
                    onBackButtonClick = {},
                    onSearchButtonClick = {},
                ),
                isLoading = false,
                isDifferentAddressesBlockVisible = false,
                tokens = flow {
                    emit(
                        PagingData.from(
                            listOf(
                                TokenListPreviewData.createManageToken(),
                                TokenListPreviewData.createManageToken(),
                            ),
                        ),
                    )
                },
                onTokensLoadStateChanged = {},
            ),
        )
    }
}