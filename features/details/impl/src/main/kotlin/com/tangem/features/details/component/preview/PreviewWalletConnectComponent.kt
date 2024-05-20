package com.tangem.features.details.component.preview

import com.tangem.features.details.component.WalletConnectComponent
import kotlinx.coroutines.flow.MutableStateFlow

internal class PreviewWalletConnectComponent : WalletConnectComponent {

    override val state = MutableStateFlow(
        value = WalletConnectComponent.State.Content(
            onClick = {},
        ),
    )
}
