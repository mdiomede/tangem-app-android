package com.tangem.feature.tokendetails.presentation.tokendetails.viewmodels

import arrow.core.getOrElse
import com.tangem.common.Provider
import com.tangem.core.analytics.api.AnalyticsEventHandler
import com.tangem.datasource.local.swaptx.ExchangeAnalyticsStatus
import com.tangem.datasource.local.swaptx.SwapTransactionStatusStore
import com.tangem.domain.appcurrency.model.AppCurrency
import com.tangem.domain.tokens.GetCryptoCurrencyStatusesSyncUseCase
import com.tangem.domain.tokens.model.CryptoCurrency
import com.tangem.domain.tokens.model.CryptoCurrencyStatus
import com.tangem.domain.tokens.models.analytics.TokenExchangeAnalyticsEvent
import com.tangem.domain.wallets.models.UserWalletId
import com.tangem.domain.wallets.usecase.GetSelectedWalletSyncUseCase
import com.tangem.feature.swap.domain.SwapRepository
import com.tangem.feature.swap.domain.SwapTransactionRepository
import com.tangem.feature.swap.domain.models.domain.ExchangeStatus
import com.tangem.feature.swap.domain.models.domain.ExchangeStatusModel
import com.tangem.feature.swap.domain.models.domain.SavedSwapTransactionListModel
import com.tangem.feature.tokendetails.presentation.tokendetails.state.SwapTransactionsState
import com.tangem.feature.tokendetails.presentation.tokendetails.state.factory.TokenDetailsSwapTransactionsStateConverter
import com.tangem.utils.coroutines.CoroutineDispatcherProvider
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

@Suppress("LongParameterList")
internal class ExchangeStatusFactory(
    private val swapTransactionRepository: SwapTransactionRepository,
    private val swapRepository: SwapRepository,
    private val getSelectedWalletSyncUseCase: GetSelectedWalletSyncUseCase,
    private val getMultiCryptoCurrencyStatusUseCase: GetCryptoCurrencyStatusesSyncUseCase,
    private val swapTransactionStatusStore: SwapTransactionStatusStore,
    private val dispatchers: CoroutineDispatcherProvider,
    private val clickIntents: TokenDetailsClickIntents,
    private val appCurrencyProvider: Provider<AppCurrency>,
    private val analyticsEventsHandlerProvider: Provider<AnalyticsEventHandler>,
    private val userWalletId: UserWalletId,
    private val cryptoCurrency: CryptoCurrency,
) {

    private val swapTransactionsStateConverter by lazy {
        TokenDetailsSwapTransactionsStateConverter(
            clickIntents = clickIntents,
            cryptoCurrency = cryptoCurrency,
            appCurrencyProvider = appCurrencyProvider,
            analyticsEventsHandlerProvider = analyticsEventsHandlerProvider,
        )
    }

    operator fun invoke() = combine(
        flow = swapTransactionRepository.getTransactions(userWalletId, cryptoCurrency.id),
        flow2 = getWalletCryptoCurrencies().conflate(),
    ) { savedTransactions, cryptoCurrenciesStatusList ->
        getExchangeStatusState(
            savedTransactions = savedTransactions,
            cryptoCurrencyStatusList = cryptoCurrenciesStatusList,
        )
    }

    private fun getWalletCryptoCurrencies() = flow {
        val selectedWallet = getSelectedWalletSyncUseCase().fold(
            ifLeft = { null },
            ifRight = { it },
        )
        requireNotNull(selectedWallet) { "No selected wallet" }

        val cryptoCurrenciesList = getMultiCryptoCurrencyStatusUseCase(selectedWallet.walletId)
            .getOrElse { emptyList() }

        emit(cryptoCurrenciesList)
    }

    suspend fun updateSwapTxStatuses(swapTxList: PersistentList<SwapTransactionsState>) = withContext(dispatchers.io) {
        swapTxList.map { tx ->
            async {
                val statusModel = getExchangeStatus(tx.txId)
                swapTransactionsStateConverter.updateTxStatus(tx, statusModel)
                tx.removeIfFinished(statusModel?.status)
            }
        }
            .awaitAll()
            .filterNotNull()
            .toPersistentList()
    }

    private suspend fun getExchangeStatus(txId: String): ExchangeStatusModel? {
        return swapRepository.getExchangeStatus(txId)
            .fold(
                ifLeft = { null },
                ifRight = {
                    sendStatusUpdateAnalytics(it)
                    it
                },
            )
    }

    private suspend fun sendStatusUpdateAnalytics(statusModel: ExchangeStatusModel) {
        val txId = statusModel.txId ?: return
        val status = toAnalyticStatus(statusModel.status) ?: return
        val savedStatus = swapTransactionStatusStore.getTransactionStatus(txId)

        if (savedStatus != status) {
            analyticsEventsHandlerProvider().send(
                TokenExchangeAnalyticsEvent.CexTxStatusChanged(cryptoCurrency.symbol, status.value),
            )
            swapTransactionStatusStore.setTransactionStatus(txId, status)
        }
    }

    private fun getExchangeStatusState(
        savedTransactions: List<SavedSwapTransactionListModel>?,
        cryptoCurrencyStatusList: List<CryptoCurrencyStatus>,
    ): PersistentList<SwapTransactionsState> {
        if (savedTransactions == null || cryptoCurrencyStatusList.isEmpty()) {
            return persistentListOf()
        }

        return swapTransactionsStateConverter.convert(
            savedTransactions = savedTransactions,
            cryptoStatusList = cryptoCurrencyStatusList,
        )
    }

    private suspend fun SwapTransactionsState.removeIfFinished(status: ExchangeStatus?) = when (status) {
        null -> null // not found
        ExchangeStatus.Refunded, ExchangeStatus.Finished -> {
            swapTransactionRepository.removeTransaction(
                userWalletId = userWalletId,
                fromCryptoCurrencyId = fromCryptoCurrencyId,
                toCryptoCurrencyId = toCryptoCurrencyId,
                txId = txId,
            )
            null
        }
        else -> {
            this
        }
    }

    private fun toAnalyticStatus(status: ExchangeStatus?): ExchangeAnalyticsStatus? {
        return when (status) {
            ExchangeStatus.New,
            ExchangeStatus.Waiting,
            ExchangeStatus.Sending,
            ExchangeStatus.Confirming,
            ExchangeStatus.Exchanging,
            -> ExchangeAnalyticsStatus.InProgress
            ExchangeStatus.Verifying -> ExchangeAnalyticsStatus.KYC
            ExchangeStatus.Failed -> ExchangeAnalyticsStatus.Fail
            ExchangeStatus.Finished -> ExchangeAnalyticsStatus.Done
            ExchangeStatus.Refunded -> ExchangeAnalyticsStatus.Refunded
            else -> null
        }
    }
}