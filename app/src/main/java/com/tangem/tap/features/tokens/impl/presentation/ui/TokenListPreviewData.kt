package com.tangem.tap.features.tokens.impl.presentation.ui

import androidx.compose.runtime.mutableStateOf
import com.tangem.blockchain.common.Blockchain
import com.tangem.tap.features.tokens.impl.presentation.states.NetworkItemState
import com.tangem.tap.features.tokens.impl.presentation.states.TokenItemState
import com.tangem.wallet.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * @author Andrew Khokhlov on 06/04/2023
 */
object TokenListPreviewData {

    fun createManageToken(): TokenItemState.ManageContent {
        return TokenItemState.ManageContent(
            name = "Tether (USDT)",
            iconUrl = "https://s3.eu-central-1.amazonaws.com/tangem.api/coins/large/tether.png",
            networks = createManageNetworksList(),
            id = "",
            symbol = "",
        )
    }

    fun createReadToken(): TokenItemState.ReadContent {
        return TokenItemState.ReadContent(
            name = "Tether (USDT)",
            iconUrl = "https://s3.eu-central-1.amazonaws.com/tangem.api/coins/large/tether.png",
            networks = createReadNetworksList(),
        )
    }

    fun createManageNetworksList(): ImmutableList<NetworkItemState.ManageContent> {
        return persistentListOf(
            NetworkItemState.ManageContent(
                name = "Ethereum",
                protocolName = "MAIN",
                iconResId = mutableStateOf(R.drawable.ic_eth_no_color),
                isMainNetwork = true,
                isAdded = mutableStateOf(true),
                id = "",
                address = null,
                onToggleClick = { _, _ -> },
                onNetworkClick = {},
                decimalCount = null,
                blockchain = Blockchain.Ethereum,
            ),
            NetworkItemState.ManageContent(
                name = "BNB SMART CHAIN",
                protocolName = "BEP20",
                iconResId = mutableStateOf(R.drawable.ic_bsc_no_color),
                isMainNetwork = false,
                isAdded = mutableStateOf(false),
                id = "",
                address = null,
                onToggleClick = { _, _ -> },
                onNetworkClick = {},
                decimalCount = null,
                blockchain = Blockchain.BSC,
            ),
        )
    }

    fun createReadNetworksList(): ImmutableList<NetworkItemState.ReadContent> {
        return persistentListOf(
            NetworkItemState.ReadContent(
                name = "Ethereum",
                protocolName = "MAIN",
                iconResId = mutableStateOf(R.drawable.ic_eth_no_color),
                isMainNetwork = true,
            ),
            NetworkItemState.ReadContent(
                name = "BNB SMART CHAIN",
                protocolName = "BEP20",
                iconResId = mutableStateOf(R.drawable.ic_bsc_no_color),
                isMainNetwork = false,
            ),
        )
    }
}