package com.tangem.feature.wallet.presentation.wallet.state.factory

import com.tangem.common.Provider
import com.tangem.domain.wallets.models.UserWallet
import com.tangem.feature.wallet.presentation.wallet.domain.WalletAdditionalInfoFactory
import com.tangem.feature.wallet.presentation.wallet.domain.getCardsCount
import com.tangem.feature.wallet.presentation.wallet.state.WalletState
import com.tangem.feature.wallet.presentation.wallet.state.components.WalletCardState
import com.tangem.feature.wallet.presentation.wallet.state.components.WalletsListConfig
import com.tangem.utils.converter.Converter
import kotlinx.collections.immutable.toImmutableList

internal class WalletUpdateCardCountConverter(
    private val currentStateProvider: Provider<WalletState>,
    private val currentWalletProvider: Provider<UserWallet>,
) : Converter<Unit, WalletState> {

    override fun convert(value: Unit): WalletState {
        return when (val state = currentStateProvider()) {
            is WalletState.ContentState -> {
                state.copySealed(
                    walletsListConfig = state.walletsListConfig.refreshCardCount(),
                )
            }
            is WalletState.Initial -> state
        }
    }

    private fun WalletsListConfig.refreshCardCount(): WalletsListConfig {
        return copy(
            wallets = wallets
                .mapIndexed { index, walletCard ->
                    if (index == selectedWalletIndex) {
                        when (walletCard) {
                            is WalletCardState.Content -> walletCard.copy(
                                additionalInfo = WalletAdditionalInfoFactory.resolve(
                                    wallet = currentWalletProvider(),
                                ),
                                cardCount = currentWalletProvider().getCardsCount(),
                            )
                            is WalletCardState.HiddenContent -> walletCard.copy(
                                additionalInfo = WalletAdditionalInfoFactory.resolve(
                                    wallet = currentWalletProvider(),
                                ),
                                cardCount = currentWalletProvider().getCardsCount(),
                            )
                            else -> walletCard
                        }
                    } else {
                        walletCard
                    }
                }
                .toImmutableList(),
        )
    }
}