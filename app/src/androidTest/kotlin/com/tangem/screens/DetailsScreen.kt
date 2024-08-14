package com.tangem.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.tangem.core.ui.test.TestTags
import com.tangem.wallet.R
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode
import io.github.kakaocup.kakao.common.utilities.getResourceString

class DetailsScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<DetailsScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag(TestTags.DETAILS_SCREEN) }
    ) {

    val walletConnectButton: KNode = child {
        hasTestTag(TestTags.DETAILS_SCREEN_ITEM)
        hasText(getResourceString(R.string.wallet_connect_title))
    }
    val scanCardButton: KNode = child {
        hasTestTag(TestTags.DETAILS_SCREEN_ITEM)
        hasText(getResourceString(R.string.scan_card_settings_button))
    }
    val linkMoreCardsButton: KNode = child {
        hasTestTag(TestTags.DETAILS_SCREEN_ITEM)
        hasText(getResourceString(R.string.details_row_title_create_backup))
    }
    val cardSettingsButton: KNode = child {
        hasTestTag(TestTags.DETAILS_SCREEN_ITEM)
        hasText(getResourceString(R.string.card_settings_title))
    }
    val appSettingsButton: KNode = child {
        hasTestTag(TestTags.DETAILS_SCREEN_ITEM)
        hasText(getResourceString(R.string.app_settings_title))
    }
    val contactSupportButton: KNode = child {
        hasTestTag(TestTags.DETAILS_SCREEN_ITEM)
        hasText(getResourceString(R.string.details_row_title_contact_to_support))
    }
    val referralProgramButton: KNode = child {
        hasTestTag(TestTags.DETAILS_SCREEN_ITEM)
        hasText(getResourceString(R.string.details_referral_title))
    }
    val toSButton: KNode = child {
        hasTestTag(TestTags.DETAILS_SCREEN_ITEM)
        hasText(getResourceString(R.string.disclaimer_title))
    }
    val socialNetworkButtons: KNode = child {
        hasTestTag(TestTags.DETAILS_SCREEN_SOCIAL_NETWORK_BUTTONS)
    }
}