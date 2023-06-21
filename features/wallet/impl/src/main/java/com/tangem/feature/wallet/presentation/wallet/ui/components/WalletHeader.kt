package com.tangem.feature.wallet.presentation.wallet.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.tangem.core.ui.res.TangemTheme
import com.tangem.feature.wallet.impl.R
import com.tangem.feature.wallet.presentation.common.WalletPreviewData
import com.tangem.feature.wallet.presentation.wallet.state.WalletCardState
import com.tangem.feature.wallet.presentation.wallet.state.WalletStateHolder
import kotlinx.collections.immutable.persistentListOf

/**
 * Wallet screen header
 *
 * @param config config
 *
 * @author Andrew Khokhlov on 30/05/2023
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun WalletHeader(config: WalletStateHolder.HeaderConfig) {
    Column(
        modifier = Modifier
            .background(color = TangemTheme.colors.background.secondary)
            .padding(bottom = TangemTheme.dimens.spacing14),
    ) {
        TopBar(onScanCardClick = config.onScanCardClick, onMoreClick = config.onMoreClick)

        CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
            val lazyListState = rememberLazyListState()
            LazyRow(
                state = lazyListState,
                contentPadding = PaddingValues(horizontal = TangemTheme.dimens.spacing16),
                horizontalArrangement = Arrangement.spacedBy(TangemTheme.dimens.spacing8),
                flingBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState),
            ) {
                items(items = config.wallets, key = WalletCardState::id) { state ->
                    WalletCard(
                        state = state,
                        modifier = Modifier.width(
                            LocalConfiguration.current.screenWidthDp.dp - TangemTheme.dimens.size32,
                        ),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(onScanCardClick: () -> Unit, onMoreClick: () -> Unit) {
    TopAppBar(
        title = {
            Icon(painter = painterResource(id = R.drawable.img_tangem_logo_90_24), contentDescription = null)
        },
        actions = {
            IconButton(onClick = onScanCardClick) {
                Icon(painter = painterResource(id = R.drawable.ic_tap_card_24), contentDescription = "Scan card")
            }
            IconButton(onClick = onMoreClick) {
                Icon(painter = painterResource(id = R.drawable.ic_more_vertical_24), contentDescription = "More")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = TangemTheme.colors.background.secondary,
            titleContentColor = TangemTheme.colors.icon.primary1,
            actionIconContentColor = TangemTheme.colors.icon.primary1,
        ),
    )
}

@Preview
@Composable
private fun Preview_WalletHeader_LightTheme(
    @PreviewParameter(WalletHeaderProvider::class) state: WalletStateHolder.HeaderConfig,
) {
    TangemTheme(isDark = false) {
        WalletHeader(state)
    }
}

@Preview
@Composable
private fun Preview_WalletHeader_DarkTheme(
    @PreviewParameter(WalletHeaderProvider::class) state: WalletStateHolder.HeaderConfig,
) {
    TangemTheme(isDark = true) {
        WalletHeader(state)
    }
}

private class WalletHeaderProvider : CollectionPreviewParameterProvider<WalletStateHolder.HeaderConfig>(
    collection = listOf(
        WalletStateHolder.HeaderConfig(
            wallets = persistentListOf(
                WalletPreviewData.walletCardContent,
                WalletPreviewData.walletCardLoading,
                WalletPreviewData.walletCardHiddenContent,
                WalletPreviewData.walletCardError,
            ),
            onScanCardClick = {},
            onMoreClick = {},
        ),
    ),
)