package com.tangem.feature.wallet.presentation.wallet.state2.transformers

import com.tangem.feature.wallet.presentation.wallet.state2.WalletScreenState

/**
 * @author Andrew Khokhlov on 24/11/2023
 */
internal class UpdateBalanceHidingModeTransformer(
    private val isHidingMode: Boolean,
) : WalletScreenStateTransformer {

    override fun transform(prevState: WalletScreenState): WalletScreenState {
        return prevState.copy(isHidingMode = isHidingMode)
    }
}
