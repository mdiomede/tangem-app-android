package com.tangem.feature.wallet.presentation.organizetokens.utils.dnd

import com.tangem.common.Provider
import com.tangem.feature.wallet.presentation.organizetokens.DraggableItem
import com.tangem.feature.wallet.presentation.organizetokens.OrganizeTokensListState
import com.tangem.feature.wallet.presentation.organizetokens.OrganizeTokensListState.Empty.items
import com.tangem.feature.wallet.presentation.organizetokens.utils.common.divideGroups
import com.tangem.feature.wallet.presentation.organizetokens.utils.common.divideTokens
import com.tangem.feature.wallet.presentation.organizetokens.utils.common.uniteItems
import com.tangem.feature.wallet.presentation.organizetokens.utils.common.updateItems
import kotlinx.collections.immutable.mutate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ItemPosition

internal class DragAndDropAdapter(
    private val scopeProvider: Provider<CoroutineScope>,
    private val stateProvider: Provider<OrganizeTokensListState>,
) {

    @Volatile
    private var currentDraggingItem: DraggableItem? = null

    private val draggableGroupsManager by lazy { DraggableGroupsManager() }

    private val scope: CoroutineScope
        get() = scopeProvider.invoke()

    private val _tokenListStateUpdates = MutableStateFlow(stateProvider())

    val tokenListStateUpdates: Flow<OrganizeTokensListState> = _tokenListStateUpdates

    fun canDragItemOver(dragOver: ItemPosition, dragging: ItemPosition): Boolean {
        val items = (_tokenListStateUpdates.value as? OrganizeTokensListState.GroupedByNetwork)
            ?.items
            ?: return true // If ungrouped then item can be moved anywhere

        val (dragOverItem, draggingItem) = findItemsToMove(
            items = items,
            moveOverItemKey = dragOver.key,
            movedItemKey = dragging.key,
        )

        if (dragOverItem == null || draggingItem == null) {
            return false
        }

        return when (draggingItem) {
            is DraggableItem.GroupHeader -> checkCanMoveHeaderOver(dragOver, dragOverItem, items.lastIndex)
            is DraggableItem.Token -> checkCanMoveTokenOver(draggingItem, dragOverItem)
            is DraggableItem.GroupPlaceholder -> false
        }
    }

    fun onItemDraggingStart(item: DraggableItem) = updateListState { list ->
        if (currentDraggingItem != null) return@updateListState list
        currentDraggingItem = item

        list.updateItems {
            when (item) {
                is DraggableItem.GroupPlaceholder -> list.items
                is DraggableItem.GroupHeader -> draggableGroupsManager.collapseGroup(list.items, item)
                is DraggableItem.Token -> when (list) {
                    is OrganizeTokensListState.GroupedByNetwork -> list.items.divideGroups(item)
                    is OrganizeTokensListState.Ungrouped -> list.items.divideTokens(item)
                    is OrganizeTokensListState.Empty -> list.items
                }
            }
        }
    }

    fun onItemDraggingEnd() = updateListState { list ->
        val movingItem = currentDraggingItem ?: return@updateListState list

        currentDraggingItem = null

        list.updateItems {
            when (movingItem) {
                is DraggableItem.GroupHeader -> draggableGroupsManager.expandGroups(items)
                is DraggableItem.Token -> items.uniteItems()
                is DraggableItem.GroupPlaceholder -> items
            }
        }
    }

    fun onItemDragged(from: ItemPosition, to: ItemPosition) = updateListState { list ->
        list.updateItems {
            it.mutate { list ->
                list.add(to.index, list.removeAt(from.index))
            }
        }
    }

    private fun updateListState(block: suspend (list: OrganizeTokensListState) -> OrganizeTokensListState) {
        scope.launch(Dispatchers.Default) {
            _tokenListStateUpdates.value = block(stateProvider())
        }
    }

    private fun findItemsToMove(
        items: List<DraggableItem>,
        moveOverItemKey: Any?,
        movedItemKey: Any?,
    ): Pair<DraggableItem?, DraggableItem?> {
        var moveOverItem: DraggableItem? = null
        var movedItem: DraggableItem? = null

        for (item in items) {
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

    private fun checkCanMoveHeaderOver(
        moveOverItemPosition: ItemPosition,
        moveOverItem: DraggableItem,
        lastItemIndex: Int,
    ): Boolean {
        // Group item can be moved only to group divider or to ages of the items list
        return when {
            moveOverItemPosition.index == 0 -> true
            moveOverItemPosition.index == lastItemIndex -> true
            moveOverItem is DraggableItem.GroupPlaceholder -> true
            else -> false
        }
    }

    private fun checkCanMoveTokenOver(item: DraggableItem.Token, moveOverItem: DraggableItem): Boolean {
        // Token item can be moved only in its group
        return when (moveOverItem) {
            is DraggableItem.GroupHeader -> false // Token item can not be moved to group item
            is DraggableItem.Token -> item.groupId == moveOverItem.groupId // Token item can not be moved over its group
            is DraggableItem.GroupPlaceholder -> false
        }
    }
}
