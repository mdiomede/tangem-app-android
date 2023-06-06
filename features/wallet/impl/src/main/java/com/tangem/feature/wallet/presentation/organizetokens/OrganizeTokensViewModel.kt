package com.tangem.feature.wallet.presentation.organizetokens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.tangem.feature.wallet.presentation.common.WalletPreviewData
import com.tangem.feature.wallet.presentation.common.state.TokenListState
import com.tangem.feature.wallet.presentation.router.InnerWalletRouter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.properties.Delegates

// FIXME: Implemented with preview data
@HiltViewModel
internal class OrganizeTokensViewModel @Inject constructor() : ViewModel() {

    var router: InnerWalletRouter by Delegates.notNull()

    var uiState: OrganizeTokensStateHolder by mutableStateOf(getInitialState())
        private set

    private fun getInitialState(): OrganizeTokensStateHolder = WalletPreviewData.organizeTokensState.copy(
        header = OrganizeTokensStateHolder.HeaderConfig(
            onSortByBalanceClick = { /* no-op */ },
            onGroupByNetworkClick = this::toggleTokensByNetworkGrouping,
        ),
    )

    private fun toggleTokensByNetworkGrouping() {
        val newListState = when (uiState.tokens) {
            is TokenListState.GroupedByNetwork -> TokenListState.Ungrouped(
                tokens = WalletPreviewData.draggableTokenList,
            )
            is TokenListState.Ungrouped -> TokenListState.GroupedByNetwork(
                groups = WalletPreviewData.draggableNetworkGroups,
            )
        }

        uiState = uiState.copy(tokens = newListState)
    }
}
