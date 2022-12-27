package com.tangem.feature.swap.models

import androidx.annotation.DrawableRes

data class SwapStateHolder(
    val sendCardData: SwapCardData,
    val receiveCardData: SwapCardData,
    val networkCurrency: String,
    val fee: FeeState = FeeState.Loaded(),
    val warnings: List<SwapWarning> = emptyList(),

    val permissionState: SwapPermissionStateHolder? = null,
    val successState: SwapSuccessStateHolder? = null,
    val selectTokenState: SwapSelectTokenStateHolder? = null,

    val swapButton: SwapButton,

    val onRefresh: () -> Unit,
    val onBackClicked: () -> Unit,
    val onChangeCardsClicked: () -> Unit,
    val onSelectTokenClick: (() -> Unit)? = null,
    val onSuccess: (() -> Unit)? = null,
    val onMaxAmountSelected: (() -> Unit)? = null,
)

data class SwapCardData(
    val type: TransactionCardType,
    val amount: String?,
    val amountEquivalent: String?,
    val tokenIconUrl: String,
    val tokenCurrency: String,
    @DrawableRes val networkIconRes: Int? = null,
    val canSelectAnotherToken: Boolean = false,
)

data class SwapButton(
    val enabled: Boolean,
    val loading: Boolean = false,
    val onClick: () -> Unit,
)

sealed interface FeeState {
    data class Loaded(
        val fee: String = "",
    ) : FeeState

    object Loading : FeeState

    data class NotEnoughFundsWarning(val fee: String) : FeeState
}

sealed interface TransactionCardType {

    data class SendCard(
        val balance: String,
        val permissionIsGiven: Boolean,
        val onAmountChanged: ((String) -> Unit),
    ) : TransactionCardType

    data class ReceiveCard(
        val highPriceImpact: String? = null,
    ) : TransactionCardType
}

sealed interface SwapWarning {
    data class PermissionNeeded(val tokenCurrency: String) : SwapWarning
    data class GenericWarning(val message: String, val onClick: () -> Unit) : SwapWarning
    // data class RateExpired(val onClick: () -> Unit) : SwapWarning
    // object HighPriceImpact : SwapWarning
}
