package com.tangem.features.details.component.preview

import com.tangem.features.details.component.DetailsComponent
import com.tangem.features.details.state.DetailsFooter
import com.tangem.features.details.state.DetailsState
import com.tangem.features.details.utils.BlocksBuilder
import com.tangem.features.details.utils.SocialsBuilder
import kotlinx.coroutines.flow.MutableStateFlow

internal class PreviewDetailsComponent : DetailsComponent {

    private val previewBlocks = BlocksBuilder(
        walletConnectComponent = PreviewWalletConnectComponent(),
        userWalletListComponent = PreviewUserWalletListComponent(),
    ).buldAll()

    private val previewFooter = DetailsFooter(
        socials = SocialsBuilder(urlOpener = { /* no-op */ }).buildAll(),
        appVersion = "1.0.0-preview",
    )

    private val previewState = DetailsState(
        blocks = previewBlocks,
        footer = previewFooter,
        popBack = { /* no-op */ },
    )

    override val state = MutableStateFlow(previewState)
}
