package com.tangem.feature.wallet.presentation.organizetokens.utils.dnd

import com.tangem.feature.wallet.presentation.organizetokens.DraggableItem
import com.tangem.feature.wallet.presentation.organizetokens.utils.common.divideGroups
import com.tangem.feature.wallet.presentation.organizetokens.utils.common.getGroupPlaceholder
import com.tangem.feature.wallet.presentation.organizetokens.utils.common.uniteItems

internal class DraggableGroupsManager {

    @Volatile
    private var groupIdToTokens: Map<String, List<DraggableItem.Token>>? = null

    fun collapseGroup(items: List<DraggableItem>, group: DraggableItem.GroupHeader): List<DraggableItem> {
        if (!groupIdToTokens.isNullOrEmpty()) return items

        groupIdToTokens = items
            .asSequence()
            .filterIsInstance<DraggableItem.Token>()
            .groupBy { it.groupId }

        val groupTokens = items.filterNot { it is DraggableItem.Token && it.groupId == group.id }

        return groupTokens.divideGroups(group)
    }

    fun expandGroups(items: List<DraggableItem>): List<DraggableItem> {
        if (groupIdToTokens.isNullOrEmpty()) return items

        val currentGroups = items.filterIsInstance<DraggableItem.GroupHeader>()
        val lastGroupIndex = currentGroups.lastIndex

        return currentGroups
            .flatMapIndexed { index, group ->
                buildList {
                    add(group)
                    addAll(groupIdToTokens?.get(group.id).orEmpty())
                    if (index != lastGroupIndex) {
                        add(getGroupPlaceholder(index))
                    }
                }
            }
            .uniteItems()
            .also { groupIdToTokens = null }
    }
}
