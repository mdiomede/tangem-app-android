package com.tangem.feature.wallet.presentation.wallet.ui.components.multicurrency

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tangem.feature.wallet.presentation.common.component.NetworkGroupItem
import com.tangem.feature.wallet.presentation.common.component.TokenItem
import com.tangem.feature.wallet.presentation.wallet.state.content.WalletTokensListState

/**
 * Multi-currency content item
 *
 * @param state    item state
 * @param modifier modifier
 *
 * @author Andrew Khokhlov on 28/07/2023
 */
@Composable
internal fun MultiCurrencyContentItem(state: WalletTokensListState.TokensListItemState, modifier: Modifier = Modifier) {
    when (state) {
        is WalletTokensListState.TokensListItemState.NetworkGroupTitle -> {
            NetworkGroupItem(networkName = state.networkName, modifier = modifier)
        }
        is WalletTokensListState.TokensListItemState.Token -> {
            TokenItem(state = state.state, modifier = modifier)
        }
    }
}