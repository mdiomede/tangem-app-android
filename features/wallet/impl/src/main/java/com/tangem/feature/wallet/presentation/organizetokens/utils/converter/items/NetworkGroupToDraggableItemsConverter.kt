package com.tangem.feature.wallet.presentation.organizetokens.utils.converter.items

import com.tangem.domain.tokens.model.NetworkGroup
import com.tangem.feature.wallet.presentation.organizetokens.DraggableItem
import com.tangem.feature.wallet.presentation.organizetokens.utils.common.getGroupHeaderId
import com.tangem.feature.wallet.presentation.organizetokens.utils.common.getGroupPlaceholder
import com.tangem.utils.converter.Converter

internal class NetworkGroupToDraggableItemsConverter(
    private val itemConverter: TokenStatusToDraggableItemConverter,
) : Converter<NetworkGroup, List<DraggableItem>> {

    override fun convert(value: NetworkGroup): List<DraggableItem> {
        return buildList {
            add(createGroupHeader(value))
            addAll(createTokens(value))
        }
    }

    override fun convertList(input: List<NetworkGroup>): List<List<DraggableItem>> {
        val lastItemIndex = input.lastIndex

        return input.mapIndexed { index, networkGroup ->
            convert(networkGroup).toMutableList()
                .also { mutableGroup ->
                    if (index != lastItemIndex) {
                        mutableGroup.add(getGroupPlaceholder(index))
                    }
                }
        }
    }

    private fun createGroupHeader(group: NetworkGroup) = DraggableItem.GroupHeader(
        id = getGroupHeaderId(group.network.id),
        networkName = group.network.name,
    )

    private fun createTokens(group: NetworkGroup): List<DraggableItem.Token> {
        return itemConverter.convertList(group.tokens.toList())
    }
}
