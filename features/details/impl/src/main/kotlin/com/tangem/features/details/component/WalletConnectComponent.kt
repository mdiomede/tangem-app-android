package com.tangem.features.details.component

import androidx.compose.runtime.Immutable
import com.tangem.core.decompose.context.AppComponentContext
import kotlinx.coroutines.flow.StateFlow

interface WalletConnectComponent {

    val state: StateFlow<State>

    @Immutable
    sealed class State {

        data object Unavailable : State()

        data class Content(val onClick: () -> Unit) : State()
    }

    interface Factory {
        fun create(context: AppComponentContext): WalletConnectComponent
    }
}
