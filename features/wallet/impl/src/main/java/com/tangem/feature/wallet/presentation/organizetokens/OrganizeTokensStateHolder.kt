package com.tangem.feature.wallet.presentation.organizetokens

import com.tangem.common.Provider
import com.tangem.domain.tokens.error.TokenListError
import com.tangem.domain.tokens.error.TokenListSortingError
import com.tangem.domain.tokens.model.TokenList
import com.tangem.feature.wallet.presentation.organizetokens.utils.common.updateSorting
import com.tangem.feature.wallet.presentation.organizetokens.utils.converter.InProgressStateConverter
import com.tangem.feature.wallet.presentation.organizetokens.utils.converter.TokenListToStateConverter
import com.tangem.feature.wallet.presentation.organizetokens.utils.converter.error.TokenListErrorConverter
import com.tangem.feature.wallet.presentation.organizetokens.utils.converter.error.TokenListSortingErrorConverter
import com.tangem.feature.wallet.presentation.organizetokens.utils.converter.items.NetworkGroupToDraggableItemsConverter
import com.tangem.feature.wallet.presentation.organizetokens.utils.converter.items.TokenListToListStateConverter
import com.tangem.feature.wallet.presentation.organizetokens.utils.converter.items.TokenStatusToDraggableItemConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Suppress("unused") // TODO: Will be used in next MR
internal class OrganizeTokensStateHolder(
    private val intents: OrganizeTokensIntents,
    private val fiatCurrencyCode: String,
    private val fiatCurrencySymbol: String,
) {

    private val stateInternal: MutableStateFlow<OrganizeTokensState> = MutableStateFlow(getInitialState())

    private val tokenListConverter by lazy {
        val tokensConverter = TokenStatusToDraggableItemConverter(
            fiatCurrencyCode = fiatCurrencyCode,
            fiatCurrencySymbol = fiatCurrencySymbol,
        )
        val itemsConverter = TokenListToListStateConverter(
            tokensConverter = tokensConverter,
            groupsConverter = NetworkGroupToDraggableItemsConverter(tokensConverter),
        )

        TokenListToStateConverter(Provider(::state), itemsConverter)
    }

    private val inProgressStateConverter by lazy {
        InProgressStateConverter()
    }

    private val tokenListErrorConverter by lazy {
        TokenListErrorConverter(Provider(::state), inProgressStateConverter)
    }

    private val tokenListSortingErrorConverter by lazy {
        TokenListSortingErrorConverter(Provider(::state), inProgressStateConverter)
    }

    val stateFlow: StateFlow<OrganizeTokensState> = stateInternal

    @Volatile
    var tokenList: TokenList? = null
        private set

    var state: OrganizeTokensState
        get() = stateInternal.value
        private set(value) {
            stateInternal.value = value
        }

    fun updateStateWithTokenList(tokenList: TokenList) {
        state = tokenListConverter.convert(tokenList)
        this.tokenList = tokenList
    }

    fun updateStateToDisplayProgress() {
        state = inProgressStateConverter.convert(state)
    }

    fun updateStateToHideProgress() {
        state = inProgressStateConverter.convertBack(state)
    }

    fun updateStateWithManualSorting(itemsState: OrganizeTokensListState) {
        state = state.copy(
            header = state.header.copy(isSortedByBalance = false),
            itemsState = itemsState,
        )
        tokenList = tokenList?.updateSorting(isSortedByBalance = false)
    }

    fun updateStateWithError(error: TokenListError) {
        state = tokenListErrorConverter.convert(error)
    }

    fun updateStateWithError(error: TokenListSortingError) {
        state = tokenListSortingErrorConverter.convert(error)
    }

    fun getInitialState(): OrganizeTokensState {
        return OrganizeTokensState(
            onBackClick = intents::onBackClick,
            itemsState = OrganizeTokensListState.Empty,
            header = OrganizeTokensState.HeaderConfig(
                onSortClick = intents::onSortClick,
                onGroupClick = intents::onGroupClick,
            ),
            actions = OrganizeTokensState.ActionsConfig(
                onApplyClick = intents::onApplyClick,
                onCancelClick = intents::onCancelClick,
            ),
            dndConfig = OrganizeTokensState.DragAndDropConfig(
                onItemDragged = intents::onItemDragged,
                onDragStart = intents::onItemDraggingStart,
                onItemDragEnd = intents::onItemDraggingEnd,
                canDragItemOver = intents::canDragItemOver,
            ),
        )
    }
}
