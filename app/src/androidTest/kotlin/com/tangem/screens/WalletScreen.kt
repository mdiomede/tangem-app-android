package com.tangem.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.tangem.tap.common.compose.resources.TestTags
import io.github.kakaocup.compose.node.element.ComposeScreen

class WalletScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<WalletScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag(TestTags.WALLET_SCREEN) }
    )
