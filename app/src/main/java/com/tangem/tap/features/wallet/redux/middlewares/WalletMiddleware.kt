package com.tangem.tap.features.wallet.redux.middlewares

import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import com.tangem.blockchain.common.Amount
import com.tangem.blockchain.common.AmountType
import com.tangem.common.CompletionResult
import com.tangem.common.core.TangemSdkError
import com.tangem.common.extensions.isZero
import com.tangem.common.services.Result
import com.tangem.operations.attestation.Attestation
import com.tangem.operations.attestation.OnlineCardVerifier
import com.tangem.tap.common.analytics.Analytics
import com.tangem.tap.common.extensions.*
import com.tangem.tap.common.redux.AppDialog
import com.tangem.tap.common.redux.AppState
import com.tangem.tap.common.redux.global.GlobalAction
import com.tangem.tap.common.redux.navigation.AppScreen
import com.tangem.tap.common.redux.navigation.NavigationAction
import com.tangem.tap.domain.extensions.toSendableAmounts
import com.tangem.tap.features.demo.DemoHelper
import com.tangem.tap.features.home.redux.HomeAction
import com.tangem.tap.features.send.redux.PrepareSendScreen
import com.tangem.tap.features.wallet.redux.*
import com.tangem.tap.network.NetworkStateChanged
import com.tangem.tap.scope
import com.tangem.tap.store
import com.tangem.tap.tangemSdkManager
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import org.rekotlin.Action
import org.rekotlin.DispatchFunction
import org.rekotlin.Middleware

class WalletMiddleware {
    private val tradeCryptoMiddleware = TradeCryptoMiddleware()
    private val warningsMiddleware = WarningsMiddleware()
    private val multiWalletMiddleware = MultiWalletMiddleware()

    val walletMiddleware: Middleware<AppState> = { dispatch, state ->
        { next ->
            { action ->
                handleAction(state, action, dispatch)
                next(action)
            }
        }
    }

    private fun handleAction(state: () -> AppState?, action: Action, dispatch: DispatchFunction) {
        if (DemoHelper.tryHandle(state, action)) return

        val globalState = store.state.globalState
        val walletState = store.state.walletState

        when (action) {
            is WalletAction.TradeCryptoAction -> tradeCryptoMiddleware.handle(state, action)
            is WalletAction.Warnings -> warningsMiddleware.handle(action, globalState)
            is WalletAction.MultiWallet -> multiWalletMiddleware.handle(
                action,
                walletState,
                globalState
            )
            is WalletAction.LoadWallet -> {
                scope.launch {
                    if (action.blockchain == null) {
                        walletState.walletManagers.map { walletManager ->
                            async { globalState.tapWalletManager.loadWalletData(walletManager) }
                        }.awaitAll()
                    } else {
                        val walletManager = walletState.getWalletManager(action.blockchain)
                            ?: action.walletManager
                        walletManager?.let { globalState.tapWalletManager.loadWalletData(it) }
                    }
                }
            }
            is WalletAction.LoadWallet.Success -> {
                val coinAmount = action.wallet.amounts[AmountType.Coin]?.value
                if (coinAmount != null && !coinAmount.isZero()) {
                    if (walletState.getWalletData(action.blockchain) == null) {
                        store.dispatch(
                            WalletAction.MultiWallet.AddBlockchain(
                                action.blockchain,
                                null
                            )
                        )
                        store.dispatch(
                            WalletAction.LoadWallet.Success(
                                action.wallet,
                                action.blockchain
                            )
                        )
                    }
                }
                store.dispatch(WalletAction.Warnings.CheckHashesCount.CheckHashesCountOnline)
                warningsMiddleware.tryToShowAppRatingWarning(action.wallet)
            }
            is WalletAction.LoadFiatRate -> {
                val tapWalletManager = globalState.tapWalletManager
                val fiatAppCurrency = globalState.appCurrency
                scope.launch {
                    when {
                        action.wallet != null -> {
                            globalState.tapWalletManager.loadFiatRate(
                                fiatCurrency = fiatAppCurrency,
                                wallet = action.wallet,
                            )
                        }
                        action.currencyList != null -> {
                            globalState.tapWalletManager.loadFiatRate(
                                fiatCurrency = fiatAppCurrency,
                                currencies = action.currencyList,
                            )
                        }
                        else -> {
                            val currencyList = walletState.walletsData.map { it.currency }
                            globalState.tapWalletManager.loadFiatRate(
                                fiatCurrency = fiatAppCurrency,
                                currencies = currencyList,
                            )
                        }
                    }
                }
            }
            is WalletAction.CreateWallet -> {
                scope.launch {
                    val result = tangemSdkManager.createWallet(
                        globalState.scanResponse?.card?.cardId
                    )
                    when (result) {
                        is CompletionResult.Success -> {
                            val scanNoteResponse =
                                globalState.scanResponse?.copy(card = result.data)
                            scanNoteResponse?.let { store.onCardScanned(scanNoteResponse) }
                        }
                        is CompletionResult.Failure -> {
                            (result.error as? TangemSdkError)?.let { error ->
                                store.state.globalState.analyticsHandlers?.logCardSdkError(
                                    error,
                                    Analytics.ActionToLog.CreateWallet,
                                    card = store.state.detailsState.scanResponse?.card
                                )
                            }
                        }
                    }
                }
            }
            is WalletAction.Scan -> {
                store.dispatch(HomeAction.ShouldScanCardOnResume(true))
                store.dispatch(NavigationAction.PopBackTo(AppScreen.Home))
            }
            is WalletAction.LoadCardInfo -> {
                val attestationFailed = action.card.attestation.status == Attestation.Status.Failed
                store.dispatchOnMain(GlobalAction.SetIfCardVerifiedOnline(!attestationFailed))

                scope.launch {
                    val response = OnlineCardVerifier().getCardInfo(
                        action.card.cardId, action.card.cardPublicKey
                    )
                    when (response) {
                        is Result.Success -> {
                            val actionList = listOf(
                                WalletAction.SetArtworkId(response.data.artwork?.id),
                                WalletAction.LoadArtwork(action.card, response.data.artwork?.id),
                            )
                            withMainContext { actionList.forEach { store.dispatch(it) } }
                        }
                        is Result.Failure -> {}
                    }
                    store.dispatchOnMain(WalletAction.Warnings.CheckIfNeeded)
                }
            }
            is WalletAction.LoadData -> {
                scope.launch {
                    val scanNoteResponse = globalState.scanResponse ?: return@launch
                    if (walletState.walletsData.isNotEmpty()) {
                        globalState.tapWalletManager.reloadData(scanNoteResponse)
                    } else {
                        globalState.tapWalletManager.loadData(scanNoteResponse)
                    }
                }
            }
            is NetworkStateChanged -> {
                globalState.scanResponse?.let { scanNoteResponse ->
                    store.dispatch(WalletAction.Warnings.CheckHashesCount.CheckHashesCountOnline)
                    scope.launch { globalState.tapWalletManager.loadData(scanNoteResponse) }
                }
            }
            is WalletAction.CopyAddress -> {
                action.context.copyToClipboard(action.address)
                store.dispatch(WalletAction.CopyAddress.Success)
            }
            is WalletAction.ShareAddress -> {
                action.context.shareText(action.address)
            }
            is WalletAction.ExploreAddress -> {
                val uri = Uri.parse(action.exploreUrl)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                ContextCompat.startActivity(action.context, intent, null)
            }
            is WalletAction.Send -> {
                val newAction = prepareSendAction(action.amount, store.state.walletState)
                store.dispatch(newAction)
                if (newAction is PrepareSendScreen) {
                    store.dispatch(NavigationAction.NavigateTo(AppScreen.Send))
                }
            }
            is WalletAction.ShowDialog.QrCode -> {
                val selectedWalletData =
                    walletState.getWalletData(walletState.selectedCurrency) ?: return
                val selectedAddressData =
                    selectedWalletData.walletAddresses?.selectedAddress ?: return

                val currency = selectedWalletData.currency
                store.dispatchDialogShow(AppDialog.AddressInfoDialog(currency, selectedAddressData))
            }
        }
    }

    private fun prepareSendAction(amount: Amount?, state: WalletState?): Action {
        val selectedWalletData = state?.getSelectedWalletData()
        val currency = selectedWalletData?.currency
        val walletStore = state?.getWalletStore(currency)

        return if (amount != null) {
            if (amount.type is AmountType.Token) {
                prepareSendActionForToken(amount, state, selectedWalletData, walletStore)
            } else {
                PrepareSendScreen(amount, selectedWalletData?.fiatRate, walletStore?.walletManager)
            }
        } else {
            val amounts = walletStore?.walletManager?.wallet?.amounts?.toSendableAmounts()
            if (currency != null && state.isMultiwalletAllowed) {
                when (currency) {
                    is Currency.Blockchain -> {
                        val amountToSend =
                            amounts?.find { it.currencySymbol == currency.blockchain.currency }
                                ?: return WalletAction.Send.ChooseCurrency(amounts)
                        PrepareSendScreen(
                            coinAmount = amountToSend,
                            coinRate = selectedWalletData.fiatRate,
                            walletManager = walletStore.walletManager
                        )
                    }
                    is Currency.Token -> {
                        val amountToSend =
                            amounts?.find { it.currencySymbol == currency.token.symbol }
                                ?: return WalletAction.Send.ChooseCurrency(amounts)
                        prepareSendActionForToken(
                            amount = amountToSend,
                            state = state,
                            selectedWalletData = selectedWalletData,
                            walletStore = walletStore
                        )
                    }
                }
            } else {
                if (amounts?.size ?: 0 > 1) {
                    WalletAction.Send.ChooseCurrency(amounts)
                } else {
                    val amountToSend = amounts?.first()
                    PrepareSendScreen(
                        coinAmount = amountToSend,
                        coinRate = selectedWalletData?.fiatRate,
                        walletManager = walletStore?.walletManager
                    )
                }
            }
        }
    }

    private fun prepareSendActionForToken(
        amount: Amount,
        state: WalletState?,
        selectedWalletData: WalletData?,
        walletStore: WalletStore?
    ): PrepareSendScreen {
        val coinRate = state?.getWalletData(walletStore?.blockchainNetwork)?.fiatRate
        val tokenRate = if (state?.isMultiwalletAllowed == true) {
            selectedWalletData?.fiatRate
        } else {
            selectedWalletData?.currencyData?.token?.fiatRate
        }
        val coinAmount = walletStore?.walletManager?.wallet?.amounts?.get(AmountType.Coin)

        return PrepareSendScreen(
            coinAmount = coinAmount,
            coinRate = coinRate,
            walletManager = walletStore?.walletManager,
            tokenAmount = amount,
            tokenRate = tokenRate
        )
    }
}

