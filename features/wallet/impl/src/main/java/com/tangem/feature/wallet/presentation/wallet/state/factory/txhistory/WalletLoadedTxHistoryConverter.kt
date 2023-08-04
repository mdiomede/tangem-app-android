package com.tangem.feature.wallet.presentation.wallet.state.factory.txhistory

import androidx.paging.PagingData
import arrow.core.Either
import com.tangem.common.Provider
import com.tangem.core.ui.components.buttons.actions.ActionButtonConfig
import com.tangem.core.ui.components.marketprice.MarketPriceBlockState
import com.tangem.domain.common.CardTypesResolver
import com.tangem.domain.txhistory.error.TxHistoryListError
import com.tangem.domain.txhistory.model.TxHistoryItem
import com.tangem.feature.wallet.presentation.wallet.state.WalletManageButton
import com.tangem.feature.wallet.presentation.wallet.state.WalletStateHolder
import com.tangem.feature.wallet.presentation.wallet.state.content.WalletTxHistoryState
import com.tangem.feature.wallet.presentation.wallet.viewmodels.WalletClickIntents
import com.tangem.utils.converter.Converter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow

/**
 * Converter from loaded tx history to [WalletTxHistoryState]
 *
 * @property currentStateProvider            current state provider
 * @property currentCardTypeResolverProvider current card type resolver provider
 * @property clickIntents                    screen click intents
 *
 * @author Andrew Khokhlov on 28/07/2023
 */
internal class WalletLoadedTxHistoryConverter(
    private val currentStateProvider: Provider<WalletStateHolder>,
    private val currentCardTypeResolverProvider: Provider<CardTypesResolver>,
    private val clickIntents: WalletClickIntents,
) : Converter<Either<TxHistoryListError, Flow<PagingData<TxHistoryItem>>>, WalletStateHolder> {

    private val walletTxHistoryItemFlowConverter by lazy {
        WalletTxHistoryItemFlowConverter(
            blockchain = currentCardTypeResolverProvider().getBlockchain(),
            clickIntents = clickIntents,
        )
    }

    override fun convert(value: Either<TxHistoryListError, Flow<PagingData<TxHistoryItem>>>): WalletStateHolder {
        return value.fold(ifLeft = ::convertError, ifRight = ::convert)
    }

    private fun convertError(error: TxHistoryListError): WalletStateHolder {
        return currentStateProvider().copySingleCurrencyContent(
            txHistoryState = when (error) {
                is TxHistoryListError.DataError -> {
                    WalletTxHistoryState.Error(onReloadClick = clickIntents::onReloadClick)
                }
            },
        )
    }

    private fun convert(items: Flow<PagingData<TxHistoryItem>>): WalletStateHolder {
        return currentStateProvider().copySingleCurrencyContent(
            txHistoryState = walletTxHistoryItemFlowConverter.convert(value = items),
        )
    }

    private fun WalletStateHolder.copySingleCurrencyContent(
        txHistoryState: WalletTxHistoryState,
    ): WalletStateHolder.SingleCurrencyContent {
        return WalletStateHolder.SingleCurrencyContent(
            onBackClick = onBackClick,
            topBarConfig = topBarConfig,
            walletsListConfig = walletsListConfig,
            pullToRefreshConfig = pullToRefreshConfig,
            notifications = notifications,
            bottomSheet = bottomSheet,
            buttons = getButtons(),
            marketPriceBlockState = getLoadingMarketPriceBlockState(),
            txHistoryState = txHistoryState,
        )
    }

    // TODO: https://tangem.atlassian.net/browse/AND-3962
    private fun getButtons(): ImmutableList<ActionButtonConfig> {
        return persistentListOf(
            WalletManageButton.Buy(onClick = {}),
            WalletManageButton.Send(onClick = {}),
            WalletManageButton.Receive(onClick = {}),
            WalletManageButton.Exchange(onClick = {}),
            WalletManageButton.CopyAddress(onClick = {}),
        )
            .map(WalletManageButton::config)
            .toImmutableList()
    }

    private fun getLoadingMarketPriceBlockState(): MarketPriceBlockState {
        return MarketPriceBlockState.Loading(currencyName = currentCardTypeResolverProvider().getBlockchain().currency)
    }
}