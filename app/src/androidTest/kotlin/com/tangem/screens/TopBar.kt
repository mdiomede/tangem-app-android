package com.tangem.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.tangem.core.ui.test.TestTags
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class TopBar(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<TopBar>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag(TestTags.MAIN_SCREEN_TOP_BAR) }
    ) {
    val moreButton: KNode = child {
        hasTestTag(TestTags.MAIN_SCREEN_MORE_BUTTON)
    }
}