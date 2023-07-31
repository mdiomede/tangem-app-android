package com.tangem.feature.wallet.presentation.organizetokens.utils.common

import com.tangem.domain.tokens.model.Network
import com.tangem.domain.tokens.model.Token

private const val TOKEN_ITEM_ID_PREFIX = "token_"
private const val GROUP_HEADER_ID_PREFIX = "group_"

internal fun getTokenItemId(tokenId: Token.ID): String = TOKEN_ITEM_ID_PREFIX + tokenId.value

internal fun getGroupHeaderId(networkId: Network.ID): String = GROUP_HEADER_ID_PREFIX + networkId.value

internal fun getTokenId(tokenItemId: String): Token.ID = Token.ID(tokenItemId.substringAfter(TOKEN_ITEM_ID_PREFIX))

internal fun getNetworkId(groupHeaderId: String): Network.ID =
    Network.ID(groupHeaderId.substringAfter(GROUP_HEADER_ID_PREFIX))
