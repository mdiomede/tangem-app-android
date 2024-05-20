package com.tangem.features.details.component

import com.tangem.core.decompose.context.AppComponentContext
import com.tangem.domain.wallets.models.UserWalletId
import com.tangem.features.details.state.DetailsState
import kotlinx.coroutines.flow.StateFlow

interface DetailsComponent {

    val state: StateFlow<DetailsState>

    data class Params(
        val selectedUserWalletId: UserWalletId,
    )

    interface Factory {

        fun create(context: AppComponentContext, params: Params): DetailsComponent
    }
}
