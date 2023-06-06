package com.tangem.feature.wallet.presentation.organizetokens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.tangem.feature.wallet.presentation.common.WalletPreviewData
import com.tangem.feature.wallet.presentation.organizetokens.OrganizeTokensStateHolder.DragConfig
import com.tangem.feature.wallet.presentation.organizetokens.OrganizeTokensStateHolder.HeaderConfig
import com.tangem.feature.wallet.presentation.organizetokens.utils.*
import com.tangem.feature.wallet.presentation.router.InnerWalletRouter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import org.burnoutcrew.reorderable.ItemPosition
import javax.inject.Inject
import kotlin.properties.Delegates

// FIXME: Implemented with preview data
@HiltViewModel
internal class OrganizeTokensViewModel @Inject constructor() : ViewModel() {

    @Volatile
    private var movingItem: DraggableItem? = null

    var router: InnerWalletRouter by Delegates.notNull()

    var uiState: OrganizeTokensStateHolder by mutableStateOf(getInitialState())
        private set

    private fun getInitialState(): OrganizeTokensStateHolder = WalletPreviewData.organizeTokensState.copy(
        itemsState = OrganizeTokensListState.Ungrouped(
            items = WalletPreviewData.draggableTokens,
        ),
        dragConfig = DragConfig(
            onItemDragged = this::moveItem,
            canDragItemOver = this::checkCanMoveItemOver,
            onItemDragEnd = this::endMoving,
            onDragStart = this::startMoving,
        ),
        header = HeaderConfig(
            onSortByBalanceClick = { /* no-op */ },
            onGroupByNetworkClick = this::toggleTokensByNetworkGrouping,
        ),
    )

    private fun toggleTokensByNetworkGrouping() {
        val newListState = when (val itemsState = uiState.itemsState) {
            is OrganizeTokensListState.GroupedByNetwork -> OrganizeTokensListState.Ungrouped(
                items = itemsState.items.filterIsInstance<DraggableItem.Token>().toPersistentList(),
            )
            is OrganizeTokensListState.Ungrouped -> OrganizeTokensListState.GroupedByNetwork(
                items = WalletPreviewData.draggableItems,
            )
        }

        uiState = uiState.copy(itemsState = newListState)
    }

    private fun checkCanMoveItemOver(moveOverItemPosition: ItemPosition, movedItemPosition: ItemPosition): Boolean {
        val items = (uiState.itemsState as? OrganizeTokensListState.GroupedByNetwork)
            ?.items
            ?: return true

        val (moveOverItem, movedItem) = items.findItemsToMove(moveOverItemPosition.key, movedItemPosition.key)

        if (moveOverItem == null || movedItem == null) {
            return false
        }

        return when (movedItem) {
            is DraggableItem.GroupHeader -> checkCanMoveHeaderOver(moveOverItemPosition, moveOverItem, items.lastIndex)
            is DraggableItem.Token -> checkCanMoveTokenOver(movedItem, moveOverItem)
            is DraggableItem.GroupDivider -> false
        }
    }

    private fun startMoving(movingItem: DraggableItem) {
        if (this.movingItem != null) return
        this.movingItem = movingItem

        val updatedItemsState = uiState.itemsState.updateItems { items ->
            when (movingItem) {
                is DraggableItem.GroupHeader -> items.collapseGroup(movingItem)
                is DraggableItem.Token -> when (uiState.itemsState) {
                    is OrganizeTokensListState.GroupedByNetwork -> items.roundGroups(movingItem)
                    is OrganizeTokensListState.Ungrouped -> items.divideItems(movingItem)
                }
                is DraggableItem.GroupDivider -> items
            }
        }

        uiState = uiState.copy(itemsState = updatedItemsState)
    }

    private fun endMoving() {
        if (movingItem == null) return

        val updatedItemsState = uiState.itemsState.updateItems { items ->
            when (movingItem) {
                is DraggableItem.GroupHeader -> items.expandGroups()
                is DraggableItem.Token -> items.uniteItems()
                is DraggableItem.GroupDivider,
                null,
                -> items
            }
        }

        uiState = uiState.copy(itemsState = updatedItemsState)
        movingItem = null
    }

    private fun moveItem(from: ItemPosition, to: ItemPosition) {
        uiState = uiState.copy(
            itemsState = uiState.itemsState.updateItems {
                it.moveItem(from.index, to.index)
            },
        )
    }
}
