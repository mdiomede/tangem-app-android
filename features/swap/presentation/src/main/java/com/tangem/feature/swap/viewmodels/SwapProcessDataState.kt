package com.tangem.feature.swap.viewmodels

import com.tangem.domain.tokens.model.CryptoCurrencyStatus
import com.tangem.feature.swap.domain.models.domain.Currency
import com.tangem.feature.swap.domain.models.domain.SwapProvider
import com.tangem.feature.swap.domain.models.ui.*

data class SwapProcessDataState(
    // Initial network id
    val networkId: String,
    @Deprecated("used in old swap mechanism")
    val fromCurrency: Currency? = null,
    @Deprecated("used in old swap mechanism")
    val toCurrency: Currency? = null,
    val fromCryptoCurrency: CryptoCurrencyStatus? = null,
    val toCryptoCurrency: CryptoCurrencyStatus? = null,
    // Amount from input
    val amount: String? = null,
    val approveDataModel: RequestApproveStateData? = null,
    val swapDataModel: SwapStateData? = null,
    val selectedFee: TxFee? = null,
    val tokensDataState: TokensDataStateExpress? = null,
    val selectedProvider: SwapProvider? = null,
    val lastLoadedSwapStates: Map<SwapProvider, SwapState> = emptyMap(),
)
