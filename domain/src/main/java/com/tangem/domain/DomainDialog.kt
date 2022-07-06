package com.tangem.domain

import com.tangem.common.extensions.VoidCallback
import com.tangem.network.api.tangemTech.CoinsResponse

/**
 * Created by Anton Zhilenkov on 10/04/2022.
 */
sealed interface DomainDialog {

    data class DialogError(val error: DomainError) : DomainDialog

    data class SelectTokenDialog(
        val items: List<CoinsResponse.Coin.Network>,
        val networkIdConverter: (String) -> String,
        val onSelect: (CoinsResponse.Coin.Network) -> Unit,
        val onClose: VoidCallback = {},
    ) : DomainDialog
}
