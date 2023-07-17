package com.tangem.feature.wallet.presentation.organizetokens.utils.converter.items

import com.tangem.domain.tokens.model.Network
import com.tangem.domain.tokens.model.Token
import com.tangem.feature.wallet.presentation.organizetokens.DraggableItem
import com.tangem.feature.wallet.presentation.organizetokens.OrganizeTokensListState
import com.tangem.feature.wallet.presentation.organizetokens.utils.common.getNetworkId
import com.tangem.feature.wallet.presentation.organizetokens.utils.common.getTokenId
import com.tangem.utils.converter.Converter

internal class ListStateToTokensIdsConverter : Converter<OrganizeTokensListState, Set<Pair<Network.ID, Token.ID>>> {

    override fun convert(value: OrganizeTokensListState): Set<Pair<Network.ID, Token.ID>> {
        val tokens = when (value) {
            is OrganizeTokensListState.Empty -> return emptySet()
            is OrganizeTokensListState.GroupedByNetwork -> value.items.filterIsInstance<DraggableItem.Token>()
            is OrganizeTokensListState.Ungrouped -> value.items
        }

        return tokens
            .map { getNetworkId(it.groupId) to getTokenId(it.tokenItemState.id) }
            .toSet()
    }
}
