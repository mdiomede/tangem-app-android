package com.tangem.feature.wallet.presentation.wallet.state2.transformers

import com.tangem.domain.wallets.models.UserWalletId
import com.tangem.feature.wallet.presentation.wallet.state2.WalletState

/**
 * @author Andrew Khokhlov on 15/11/2023
 */
internal class CloseBottomSheetTransformer(userWalletId: UserWalletId) : WalletStateTransformer(userWalletId) {

    override fun transform(prevState: WalletState): WalletState {
        return when (prevState) {
            is WalletState.MultiCurrency.Content -> {
                prevState.copy(bottomSheetConfig = prevState.bottomSheetConfig?.copy(isShow = false))
            }
            is WalletState.MultiCurrency.Locked -> prevState.copy(isBottomSheetShow = false)
            is WalletState.SingleCurrency.Content -> {
                prevState.copy(bottomSheetConfig = prevState.bottomSheetConfig?.copy(isShow = false))
            }
            is WalletState.SingleCurrency.Locked -> prevState.copy(isBottomSheetShow = false)
        }
    }
}
