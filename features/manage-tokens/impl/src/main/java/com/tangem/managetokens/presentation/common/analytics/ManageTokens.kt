package com.tangem.managetokens.presentation.common.analytics

import com.tangem.core.analytics.models.AnalyticsEvent
import com.tangem.core.analytics.models.AnalyticsParam

sealed class ManageTokens(
    event: String,
    params: Map<String, String> = emptyMap(),
) : AnalyticsEvent("Manage Tokens", event, params) {

    class ScreenOpened : ManageTokens("Manage Tokens Screen Opened")

    class TokenIsNotFound(userInput: String) : ManageTokens("Token Is Not Found")

    class TokenSwitcherChanged(
        token: String,
        state: AnalyticsParam.OnOffState,
    ) : ManageTokens(
        "Token Switcher Changed",
        params = mapOf(
            "Token" to token,
            "State" to state.value,
        ),
    )

    class ButtonAdd(token: String) : ManageTokens(
        "Button - Add",
        params = mapOf("Token" to token)
    )

    class ButtonEdit(token: String) : ManageTokens(
        "Button - Edit",
        params = mapOf("Token" to token)
    )

    object ButtonChooseWallet : ManageTokens(event = "Button - Choose Wallet")

    class WalletSelected(source: Source) : ManageTokens(
        event = "Wallet Selected",
        params = mapOf("Source" to source.name)
    ) {

        enum class Source(name: String) {
            MainToken("Main Token"),
            CustomToken("Custom Token"),
        }
    }

    class TokensAdded(token: String) : ManageTokens(
        "Tokens Added",
        params = mapOf("Token" to token)
    )

    object NoticeNonNativeNetworkClicked : ManageTokens(event = "Notice - Non Native Network Clicked")

    class ButtonGenerateAddresses(cardCount: Int) : ManageTokens(
        "Button - Generate Addresses",
        params = mapOf("CardCount" to cardCount.toString())
    )

    object ButtonCustomToken : ManageTokens("Button - Custom Token")

    class TokenWasAdded(
        val derivationPath: String,
        val networkId: String,
        val token: String?,
        val contractAddress: String?,
    ) : ManageTokens(
        event = "Custom Token Was Added",
        params = mutableMapOf(
            "Derivation Path" to derivationPath,
            "Network Id" to networkId,
        ).apply {
            token?.let { put("Token", it) }
            contractAddress?.let { put("Contract Address", it) }
        }
    )

    class CustomTokenNetworkSelected(blockchain: String) : ManageTokens(
        "Custom Token Network Selected",
        params = mapOf("blockchain" to blockchain)
    )

    class CustomTokenDerivationSelected(derivation: String) : ManageTokens(
        "Custom Token Derivation Selected",
        params = mapOf("Derivation" to derivation)
    )

    class CustomTokenAddress(validated: Boolean) : ManageTokens(
        "Custom Token Address",
        params = mapOf("Validation" to if (validated) "Ok" else "Error")
    )

    object CustomTokenName : ManageTokens("Custom Token Name")

    object CustomTokenSymbol : ManageTokens("Custom Token Symbol")

    object CustomTokenDecimals : ManageTokens("Custom Token Decimals")

}