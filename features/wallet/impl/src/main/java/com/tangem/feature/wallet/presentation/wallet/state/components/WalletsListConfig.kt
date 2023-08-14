package com.tangem.feature.wallet.presentation.wallet.state.components

import kotlinx.collections.immutable.ImmutableList

/**
 * Wallets list config
 *
 * @property selectedWalletIndex selected wallet index
 * @property wallets             wallets list
 * @property onWalletChange      lambda be invoked when wallet is swiped
 *
 * @author Andrew Khokhlov on 24/06/2023
 */
internal data class WalletsListConfig(
    val selectedWalletIndex: Int,
    val wallets: ImmutableList<WalletCardState>,
    val onWalletChange: (Int) -> Unit,
)