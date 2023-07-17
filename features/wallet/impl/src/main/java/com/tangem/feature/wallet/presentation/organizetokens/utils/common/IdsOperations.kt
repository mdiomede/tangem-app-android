package com.tangem.feature.wallet.presentation.organizetokens.utils.common

import com.tangem.domain.tokens.model.Network
import com.tangem.domain.tokens.model.Token

internal fun getTokenItemId(tokenId: Token.ID): String = tokenId.value

internal fun getGroupHeaderId(networkId: Network.ID): String = networkId.value

internal fun getTokenId(tokenItemId: String): Token.ID = Token.ID(tokenItemId)

internal fun getNetworkId(groupHeaderId: String): Network.ID = Network.ID(groupHeaderId)
