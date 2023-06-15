package com.tangem.feature.wallet.presentation.organizetokens.utils

import com.tangem.feature.wallet.presentation.organizetokens.DraggableItem
import org.burnoutcrew.reorderable.ItemPosition

internal fun List<DraggableItem.Token>.group(): List<DraggableItem> {
    val groupedTokens = this
        .groupBy { it.tokenGroupState }
    val groups = groupedTokens.keys
    val lastGroupIndex = groups.size - 1

    return groups
        .flatMapIndexed { index, group ->
            buildList {
                add(DraggableItem.GroupHeader(group))
                addAll(groupedTokens[group].orEmpty())
                if (index != lastGroupIndex) {
                    add(DraggableItem.GroupDivider(id = getGroupDividerId(index)))
                }
            }
        }
        .uniteItems()
}

internal fun List<DraggableItem>.ungroup(): List<DraggableItem.Token> {
    return this.filterIsInstance<DraggableItem.Token>()
}

@Suppress("UNCHECKED_CAST")
internal fun List<DraggableItem>.ungroupAndSortByBalance(): List<DraggableItem.Token> {
    return this
        .filterIsInstance<DraggableItem.Token>()
        .sortedByDescending { item ->
            // FIXME: May be move its to domain
            item.tokenItemState.fiatAmount
                .filter(Char::isDigit)
                .toInt()
        }
        .uniteItems() as List<DraggableItem.Token>
}

internal fun List<DraggableItem>.findItemsToMove(
    moveOverItemKey: Any?,
    movedItemKey: Any?,
): Pair<DraggableItem?, DraggableItem?> {
    var moveOverItem: DraggableItem? = null
    var movedItem: DraggableItem? = null

    for (item in this) {
        if (item.id == moveOverItemKey) {
            moveOverItem = item
        }
        if (item.id == movedItemKey) {
            movedItem = item
        }
        if (moveOverItem != null && movedItem != null) {
            break
        }
    }

    return Pair(moveOverItem, movedItem)
}

internal fun checkCanMoveHeaderOver(
    moveOverItemPosition: ItemPosition,
    moveOverItem: DraggableItem,
    lastItemIndex: Int,
): Boolean {
    // Group item can be moved only to group divider or to ages of the items list
    return when {
        moveOverItemPosition.index == 0 -> true
        moveOverItemPosition.index == lastItemIndex -> true
        moveOverItem is DraggableItem.GroupDivider -> true
        else -> false
    }
}

internal fun checkCanMoveTokenOver(item: DraggableItem.Token, moveOverItem: DraggableItem): Boolean {
    // Token item can be moved only in its group
    return when (moveOverItem) {
        is DraggableItem.GroupHeader -> false
        is DraggableItem.Token -> item.tokenGroupState.id == moveOverItem.tokenGroupState.id
        is DraggableItem.GroupDivider -> false
    }
}

internal fun List<DraggableItem>.moveItem(fromIndex: Int, toIndex: Int): List<DraggableItem> {
    return this.toMutableList()
        .apply { add(toIndex, removeAt(fromIndex)) }
}

internal fun List<DraggableItem>.divideItems(movingItem: DraggableItem): List<DraggableItem> {
    return this.map {
        it
            .roundingMode(DraggableItem.RoundingMode.All(showGap = true))
            .showShadow(show = it.id == movingItem.id)
    }
}

internal fun List<DraggableItem>.uniteItems(): List<DraggableItem> {
    val lastItemIndex = this.lastIndex
    return this.mapIndexed { index, item ->
        val mode = when (index) {
            0 -> DraggableItem.RoundingMode.Top()
            lastItemIndex -> DraggableItem.RoundingMode.Bottom()
            else -> DraggableItem.RoundingMode.None
        }

        item
            .roundingMode(mode)
            .showShadow(show = false)
    }
}

@Volatile
private var groupIdToTokens: Map<String, List<DraggableItem.Token>>? = null

internal fun List<DraggableItem>.collapseGroup(group: DraggableItem.GroupHeader): List<DraggableItem> {
    if (!groupIdToTokens.isNullOrEmpty()) return this

    groupIdToTokens = this
        .asSequence()
        .filterIsInstance<DraggableItem.Token>()
        .groupBy { it.tokenGroupState.id }

    return this
        .filterNot { it is DraggableItem.Token && it.groupId == group.id }
        .roundGroups(group)
}

internal fun List<DraggableItem>.expandGroups(): List<DraggableItem> {
    if (groupIdToTokens.isNullOrEmpty()) return this

    val currentGroups = this.filterIsInstance<DraggableItem.GroupHeader>()
    val lastGroupIndex = currentGroups.lastIndex

    return currentGroups
        .flatMapIndexed { index, group ->
            buildList {
                add(group)
                addAll(groupIdToTokens?.get(group.id).orEmpty())
                if (index != lastGroupIndex) {
                    add(DraggableItem.GroupDivider(id = getGroupDividerId(index)))
                }
            }
        }
        .uniteItems()
        .also { groupIdToTokens = null }
}

internal fun List<DraggableItem>.roundGroups(movingItem: DraggableItem): List<DraggableItem> {
    val lastItemIndex = this.lastIndex

    return this.mapIndexed { index, item ->
        when {
            item.id == movingItem.id -> {
                item
                    .roundingMode(DraggableItem.RoundingMode.All(showGap = true))
                    .showShadow(show = true)
            }
            movingItem is DraggableItem.Token && item.id == movingItem.groupId -> {
                item
                    .roundingMode(DraggableItem.RoundingMode.All(showGap = true))
                    .showShadow(show = true)
            }
            movingItem is DraggableItem.Token &&
                item is DraggableItem.Token && item.groupId == movingItem.groupId -> {
                item
                    .roundingMode(DraggableItem.RoundingMode.All(showGap = true))
                    .showShadow(show = false)
            }
            index == 0 -> {
                item
                    .roundingMode(DraggableItem.RoundingMode.Top())
                    .showShadow(show = false)
            }
            index == lastItemIndex -> {
                item
                    .roundingMode(DraggableItem.RoundingMode.Bottom())
                    .showShadow(show = false)
            }
            this[index - 1] is DraggableItem.GroupDivider -> {
                item
                    .roundingMode(DraggableItem.RoundingMode.Top(showGap = true))
                    .showShadow(show = false)
            }
            this[index + 1] is DraggableItem.GroupDivider -> {
                item
                    .roundingMode(DraggableItem.RoundingMode.Bottom(showGap = true))
                    .showShadow(show = false)
            }
            else -> {
                item
                    .roundingMode(DraggableItem.RoundingMode.None)
                    .showShadow(show = false)
            }
        }
    }
}

private fun getGroupDividerId(index: Int): String = "group_divider_$index"
