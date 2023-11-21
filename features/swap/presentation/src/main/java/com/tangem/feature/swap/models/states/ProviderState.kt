package com.tangem.feature.swap.models.states

import com.tangem.core.ui.extensions.TextReference

sealed class ProviderState {

    abstract val onProviderClick: ((String) -> Unit)?
    abstract val id: String

    data class Empty(
        override val id: String = "",
        override val onProviderClick: ((String) -> Unit)? = null,
    ) : ProviderState()

    data class Loading(
        override val id: String = "",
        override val onProviderClick: ((String) -> Unit)? = null,
    ) : ProviderState()

    data class Content(
        override val id: String,
        val name: String,
        val type: String,
        val iconUrl: String,
        val subtitle: TextReference,
        val selectionType: SelectionType,
        val additionalBadge: AdditionalBadge,
        val percentLowerThenBest: Float = 0f,
        override val onProviderClick: (String) -> Unit,
    ) : ProviderState()

    data class Unavailable(
        override val id: String,
        val name: String,
        val type: String,
        val iconUrl: String,
        val alertText: TextReference,
        val selectionType: SelectionType,
        override val onProviderClick: ((String) -> Unit)? = null,
    ) : ProviderState()

    sealed class AdditionalBadge {
        object BestTrade : AdditionalBadge()
        object Empty : AdditionalBadge()
        object PermissionRequired : AdditionalBadge()
    }

    enum class SelectionType {
        NONE, CLICK, SELECT
    }
}

object ProviderPercentDiffComparator : Comparator<ProviderState> {
    override fun compare(o1: ProviderState, o2: ProviderState): Int {
        if (o1 is ProviderState.Content && o2 !is ProviderState.Content) {
            return 1
        }
        if (o1 !is ProviderState.Content && o2 is ProviderState.Content) {
            return -1
        }
        return if (o1 is ProviderState.Content && o2 is ProviderState.Content) {
            o1.percentLowerThenBest.compareTo(o2.percentLowerThenBest)
        } else {
            0
        }
    }
}