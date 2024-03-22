package com.tangem.feature.wallet.presentation.wallet.state.transformers

import com.tangem.domain.wallets.models.UserWallet
import com.tangem.domain.wallets.models.UserWalletId
import com.tangem.feature.wallet.presentation.wallet.state.model.WalletScreenState
import com.tangem.feature.wallet.presentation.wallet.state.utils.WalletLoadingStateFactory
import com.tangem.feature.wallet.presentation.wallet.viewmodels.intents.WalletClickIntents
import kotlinx.collections.immutable.toImmutableList

/**
 * @author Andrew Khokhlov on 23/11/2023
 */
internal class ReinitializeWalletTransformer(
    private val prevWalletId: UserWalletId,
    private val newUserWallet: UserWallet,
    private val clickIntents: WalletClickIntents,
) : WalletScreenStateTransformer {

    private val walletLoadingStateFactory by lazy { WalletLoadingStateFactory(clickIntents = clickIntents) }

    override fun transform(prevState: WalletScreenState): WalletScreenState {
        return prevState.copy(
            wallets = prevState.wallets
                .filterNot { it.walletCardState.id == prevWalletId }
                .plus(element = walletLoadingStateFactory.create(newUserWallet))
                .toImmutableList(),
        )
    }
}
