package com.tangem.feature.wallet.presentation.organizetokens.utils.common

import com.tangem.feature.wallet.presentation.organizetokens.DraggableItem

@Suppress("UNCHECKED_CAST") // Erased type
internal fun <T : DraggableItem> List<T>.uniteItems(): List<T> {
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
    } as List<T>
}

internal fun List<DraggableItem.Token>.divideTokens(movingItem: DraggableItem): List<DraggableItem.Token> {
    return this.map { token ->
        token
            .roundingMode(DraggableItem.RoundingMode.All(showGap = true))
            .showShadow(show = token.id == movingItem.id) as DraggableItem.Token
    }
}

/**
 * Applies the correct [DraggableItem.RoundingMode] and shadow status to each item in the list,
 * based on the relationship of each item to the [movingItem] and its position in the list.
 *
 * @param movingItem The item that is being dragged/moved.
 * @return A list of [DraggableItem]s with updated rounding modes and shadow statuses.
 */
internal fun List<DraggableItem>.divideGroups(movingItem: DraggableItem): List<DraggableItem> {
    val lastItemIndex = this.lastIndex

    return this.mapIndexed { index, item ->
        when {
            // Case when current item is the moving item
            item.id == movingItem.id -> {
                item
                    .roundingMode(DraggableItem.RoundingMode.All(showGap = true))
                    .showShadow(show = true)
            }
            // Case when moving item is a token and current item is the group of the moving token
            movingItem is DraggableItem.Token && item.id == movingItem.groupId -> {
                item
                    .roundingMode(DraggableItem.RoundingMode.All(showGap = true))
                    .showShadow(show = true)
            }
            // Case when both moving item and current item are tokens and belong to the same group
            movingItem is DraggableItem.Token &&
                item is DraggableItem.Token && item.groupId == movingItem.groupId -> {
                item
                    .roundingMode(DraggableItem.RoundingMode.All(showGap = true))
                    .showShadow(show = false)
            }
            // Case when current item is the first item in the list
            index == 0 -> {
                item
                    .roundingMode(DraggableItem.RoundingMode.Top())
                    .showShadow(show = false)
            }
            // Case when current item is the last item in the list
            index == lastItemIndex -> {
                item
                    .roundingMode(DraggableItem.RoundingMode.Bottom())
                    .showShadow(show = false)
            }
            // Case when previous item is a GroupPlaceholder
            this[index - 1] is DraggableItem.GroupPlaceholder -> {
                item
                    .roundingMode(DraggableItem.RoundingMode.Top(showGap = true))
                    .showShadow(show = false)
            }
            // Case when next item is a GroupPlaceholder
            this[index + 1] is DraggableItem.GroupPlaceholder -> {
                item
                    .roundingMode(DraggableItem.RoundingMode.Bottom(showGap = true))
                    .showShadow(show = false)
            }
            // Default case when none of the above conditions are met
            else -> {
                item
                    .roundingMode(DraggableItem.RoundingMode.None)
                    .showShadow(show = false)
            }
        }
    }
}
