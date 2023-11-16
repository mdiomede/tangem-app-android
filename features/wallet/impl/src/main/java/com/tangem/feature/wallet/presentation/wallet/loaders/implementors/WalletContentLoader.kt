package com.tangem.feature.wallet.presentation.wallet.loaders.implementors

import com.tangem.domain.wallets.models.UserWalletId
import com.tangem.feature.wallet.presentation.wallet.subscribers.WalletSubscriber

/**
 * @author Andrew Khokhlov on 21/11/2023
 */
internal abstract class WalletContentLoader(val id: UserWalletId) {

    val subscribers: List<WalletSubscriber<*>> get() = create()

    protected abstract fun create(): List<WalletSubscriber<*>>
}
