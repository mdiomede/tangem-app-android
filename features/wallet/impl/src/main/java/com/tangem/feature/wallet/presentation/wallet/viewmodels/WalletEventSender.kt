package com.tangem.feature.wallet.presentation.wallet.viewmodels

import com.tangem.core.ui.event.consumedEvent
import com.tangem.feature.wallet.presentation.wallet.state.WalletEvent
import com.tangem.feature.wallet.presentation.wallet.state2.WalletStateHolderV2
import com.tangem.feature.wallet.presentation.wallet.state2.transformers.SendEventTransformer
import javax.inject.Inject

/**
 * @author Andrew Khokhlov on 24/11/2023
 */
internal class WalletEventSender @Inject constructor(
    private val stateHolder: WalletStateHolderV2,
) {

    fun send(event: WalletEvent) {
        stateHolder.update(transformer = SendEventTransformer(event = event, onConsume = ::onConsume))
    }

    private fun onConsume() {
        stateHolder.update {
            it.copy(event = consumedEvent())
        }
    }
}
