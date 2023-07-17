package com.tangem.feature.wallet.presentation.organizetokens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tangem.common.Provider
import com.tangem.domain.tokens.ApplyTokenListSortingUseCase
import com.tangem.domain.tokens.GetTokenListUseCase
import com.tangem.domain.tokens.ToggleTokenListGroupingUseCase
import com.tangem.domain.tokens.ToggleTokenListSortingUseCase
import com.tangem.domain.wallets.models.UserWalletId
import com.tangem.feature.wallet.presentation.organizetokens.utils.converter.items.ListStateToTokensIdsConverter
import com.tangem.feature.wallet.presentation.organizetokens.utils.dnd.DragAndDropAdapter
import com.tangem.feature.wallet.presentation.router.InnerWalletRouter
import com.tangem.feature.wallet.presentation.router.WalletRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ItemPosition
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
internal class OrganizeTokensViewModel @Inject constructor(
    private val getTokenListUseCase: GetTokenListUseCase,
    private val toggleTokenListGroupingUseCase: ToggleTokenListGroupingUseCase,
    private val toggleTokenListSortingUseCase: ToggleTokenListSortingUseCase,
    private val applyTokenListSortingUseCase: ApplyTokenListSortingUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel(), OrganizeTokensIntents {

    private val dragAndDropAdapter by lazy {
        DragAndDropAdapter(
            scopeProvider = Provider(::viewModelScope),
            stateProvider = Provider(stateHolder.state::itemsState),
        )
    }

    private val stateHolder by lazy {
        OrganizeTokensStateHolder(
            intents = this,
            fiatCurrencyCode = "USD", // TODO: https://tangem.atlassian.net/browse/AND-4006
            fiatCurrencySymbol = "$", // TODO: https://tangem.atlassian.net/browse/AND-4006
        )
    }

    var router: InnerWalletRouter by Delegates.notNull()
    val userWalletId: UserWalletId by lazy {
        val userWalletIdValue: String = checkNotNull(savedStateHandle[WalletRoute.userWalletIdKey])

        UserWalletId(userWalletIdValue)
    }

    val uiState: StateFlow<OrganizeTokensState> = stateHolder.stateFlow
        .onSubscription {
            bootstrapTokenListUpdates()
            bootstrapDragAndDropUpdates()
        }
        .stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(), stateHolder.getInitialState())

    override fun onBackClick() {
        router.popBackStack()
    }

    override fun onSortClick() {
        viewModelScope.launch(Dispatchers.Default) {
            val list = stateHolder.tokenList ?: return@launch

            toggleTokenListSortingUseCase(list).fold(
                ifLeft = stateHolder::updateStateWithError,
                ifRight = stateHolder::updateStateWithTokenList,
            )
        }
    }

    override fun onGroupClick() {
        viewModelScope.launch(Dispatchers.Default) {
            val list = stateHolder.tokenList ?: return@launch

            toggleTokenListGroupingUseCase(list).fold(
                ifLeft = stateHolder::updateStateWithError,
                ifRight = stateHolder::updateStateWithTokenList,
            )
        }
    }

    override fun onApplyClick() {
        viewModelScope.launch(Dispatchers.Default) {
            stateHolder.updateStateToDisplayProgress()

            val items = stateHolder.state.itemsState
            val converter = ListStateToTokensIdsConverter()

            val result = applyTokenListSortingUseCase(
                userWalletId = userWalletId,
                sortedTokensIds = converter.convert(items),
                isGroupedByNetwork = items is OrganizeTokensListState.GroupedByNetwork,
                isSortedByBalance = stateHolder.state.header.isSortedByBalance,
            )

            result.fold(
                ifLeft = stateHolder::updateStateWithError,
                ifRight = { stateHolder.updateStateToHideProgress() },
            )
        }
    }

    override fun onCancelClick() {
        router.popBackStack()
    }

    override fun onItemDragged(from: ItemPosition, to: ItemPosition) {
        dragAndDropAdapter.onItemDragged(from, to)
    }

    override fun canDragItemOver(dragOver: ItemPosition, dragging: ItemPosition): Boolean {
        return dragAndDropAdapter.canDragItemOver(dragOver, dragging)
    }

    override fun onItemDraggingStart(item: DraggableItem) {
        dragAndDropAdapter.onItemDraggingStart(item)
    }

    override fun onItemDraggingEnd() {
        dragAndDropAdapter.onItemDraggingEnd()
    }

    private fun bootstrapTokenListUpdates() {
        viewModelScope.launch(Dispatchers.Default) {
            getTokenListUseCase(userWalletId).first().fold(
                ifLeft = stateHolder::updateStateWithError,
                ifRight = stateHolder::updateStateWithTokenList,
            )
        }
    }

    private fun bootstrapDragAndDropUpdates() {
        dragAndDropAdapter.tokenListStateUpdates
            .distinctUntilChanged()
            .onEach(stateHolder::updateStateWithManualSorting)
            .flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }
}
