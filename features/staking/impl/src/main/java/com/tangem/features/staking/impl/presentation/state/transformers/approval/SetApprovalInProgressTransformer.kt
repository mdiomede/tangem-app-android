package com.tangem.features.staking.impl.presentation.state.transformers.approval

import com.tangem.core.ui.extensions.stringReference
import com.tangem.features.staking.impl.presentation.state.StakingNotification
import com.tangem.features.staking.impl.presentation.state.StakingStates
import com.tangem.features.staking.impl.presentation.state.StakingUiState
import com.tangem.utils.transformer.Transformer
import kotlinx.collections.immutable.toPersistentList

internal object SetApprovalInProgressTransformer : Transformer<StakingUiState> {
    override fun transform(prevState: StakingUiState): StakingUiState {
        val state = prevState.confirmationState as? StakingStates.ConfirmationState.Data
        val notifications = state?.notifications?.toMutableList() ?: mutableListOf()

        // todo staking AND-7962 replace with correct text
        notifications.add(
            StakingNotification.Warning.TransactionInProgress(
                title = stringReference("//TODO"),
                description = stringReference("//TODO"),
            ),
        )

        val updatedConfirmationState = state?.copy(
            notifications = notifications.toPersistentList(),
        ) ?: prevState.confirmationState

        return prevState.copy(
            confirmationState = updatedConfirmationState,
        )
    }
}
