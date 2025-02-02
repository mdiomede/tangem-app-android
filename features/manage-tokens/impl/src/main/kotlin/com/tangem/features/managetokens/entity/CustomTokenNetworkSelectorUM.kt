package com.tangem.features.managetokens.entity

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
internal data class CustomTokenNetworkSelectorUM(
    val showTitle: Boolean,
    val networks: ImmutableList<CurrencyNetworkUM>,
)
