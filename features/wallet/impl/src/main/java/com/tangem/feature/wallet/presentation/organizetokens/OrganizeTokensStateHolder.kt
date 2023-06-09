package com.tangem.feature.wallet.presentation.organizetokens

import androidx.compose.runtime.Immutable
import com.tangem.feature.wallet.presentation.common.state.NetworkGroupState
import com.tangem.feature.wallet.presentation.common.state.TokenItemState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import org.burnoutcrew.reorderable.ItemPosition

internal data class OrganizeTokensStateHolder(
    val header: HeaderConfig,
    val itemsState: OrganizeTokensListState,
    val dragConfig: DragConfig,
    val actions: ActionsConfig,
) {

    data class HeaderConfig(
        val onSortByBalanceClick: () -> Unit,
        val onGroupByNetworkClick: () -> Unit,
    )

    data class ActionsConfig(
        val onApplyClick: () -> Unit,
        val onCancelClick: () -> Unit,
    )

    data class DragConfig(
        val onItemDragged: (from: ItemPosition, to: ItemPosition) -> Unit,
        val canDragItemOver: (dragOver: ItemPosition, dragging: ItemPosition) -> Boolean,
        val onItemDragEnd: () -> Unit,
        val onDragStart: (item: DraggableItem) -> Unit,
    )
}

@Immutable
internal sealed class OrganizeTokensListState {
    abstract val items: ImmutableList<DraggableItem>

    data class GroupedByNetwork(
        override val items: ImmutableList<DraggableItem>,
    ) : OrganizeTokensListState()

    data class Ungrouped(
        override val items: ImmutableList<DraggableItem.Token>,
    ) : OrganizeTokensListState()

    @Suppress("UNCHECKED_CAST")
    inline fun updateItems(update: (List<DraggableItem>) -> List<DraggableItem>): OrganizeTokensListState {
        val updatedItems = update(this.items).toPersistentList()

        return when (this) {
            is GroupedByNetwork -> this.copy(items = updatedItems)
            is Ungrouped -> this.copy(items = updatedItems as ImmutableList<DraggableItem.Token>)
        }
    }
}

@Immutable
internal sealed interface DraggableItem {
    val id: String

    data class GroupHeader(
        val groupState: NetworkGroupState.Draggable,
    ) : DraggableItem {
        override val id: String = groupState.id
    }

    data class Token(
        val tokenItemState: TokenItemState.Draggable,
        val groupId: String,
    ) : DraggableItem {
        override val id: String = tokenItemState.id
    }

    data class GroupDivider(
        override val id: String,
    ) : DraggableItem
}
