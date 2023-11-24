package com.tangem.features.send.impl.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import androidx.paging.PagingData
import arrow.core.getOrElse
import com.tangem.blockchain.blockchains.xrp.XrpAddressService
import com.tangem.blockchain.common.Blockchain
import com.tangem.blockchain.common.address.Address
import com.tangem.blockchain.common.transaction.TransactionFee
import com.tangem.core.ui.utils.BigDecimalFormatter
import com.tangem.domain.appcurrency.GetSelectedAppCurrencyUseCase
import com.tangem.domain.appcurrency.model.AppCurrency
import com.tangem.domain.tokens.GetCryptoCurrenciesUseCase
import com.tangem.domain.tokens.GetCurrencyStatusUpdatesUseCase
import com.tangem.domain.tokens.model.CryptoCurrency
import com.tangem.domain.tokens.model.CryptoCurrencyStatus
import com.tangem.domain.transaction.usecase.GetFeeUseCase
import com.tangem.domain.txhistory.models.TxHistoryItem
import com.tangem.domain.txhistory.usecase.GetTxHistoryItemsCountUseCase
import com.tangem.domain.txhistory.usecase.GetTxHistoryItemsUseCase
import com.tangem.domain.walletmanager.WalletManagersFacade
import com.tangem.domain.wallets.models.UserWallet
import com.tangem.domain.wallets.models.UserWalletId
import com.tangem.domain.wallets.usecase.GetUserWalletUseCase
import com.tangem.domain.wallets.usecase.GetWalletsUseCase
import com.tangem.features.send.api.navigation.SendRouter
import com.tangem.features.send.impl.presentation.domain.AvailableWallet
import com.tangem.features.send.impl.presentation.state.SendStateFactory
import com.tangem.features.send.impl.presentation.state.SendUiState
import com.tangem.features.send.impl.presentation.state.SendUiStateType
import com.tangem.features.send.impl.presentation.state.StateRouter
import com.tangem.features.send.impl.presentation.state.fee.FeeSelectorState
import com.tangem.features.send.impl.presentation.state.fee.FeeType
import com.tangem.utils.Provider
import com.tangem.utils.coroutines.CoroutineDispatcherProvider
import com.tangem.utils.coroutines.JobHolder
import com.tangem.utils.coroutines.saveIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.properties.Delegates

@Suppress("LongParameterList")
@HiltViewModel
internal class SendViewModel @Inject constructor(
    private val dispatchers: CoroutineDispatcherProvider,
    private val getUserWalletUseCase: GetUserWalletUseCase,
    private val getCurrencyStatusUpdatesUseCase: GetCurrencyStatusUpdatesUseCase,
    private val getSelectedAppCurrencyUseCase: GetSelectedAppCurrencyUseCase,
    private val getWalletsUseCase: GetWalletsUseCase,
    private val getCryptoCurrenciesUseCase: GetCryptoCurrenciesUseCase,
    private val txHistoryItemsUseCase: GetTxHistoryItemsUseCase,
    private val txHistoryItemsCountUseCase: GetTxHistoryItemsCountUseCase,
    private val getFeeUseCase: GetFeeUseCase,
    private val walletManagersFacade: WalletManagersFacade,
    savedStateHandle: SavedStateHandle,
) : ViewModel(), DefaultLifecycleObserver, SendClickIntents {

    private val userWalletId: UserWalletId = savedStateHandle.get<String>(SendRouter.USER_WALLET_ID_KEY)
        ?.let { stringValue -> UserWalletId(stringValue) }
        ?: error("This screen can't open without `UserWalletId`")

    private val cryptoCurrency: CryptoCurrency = savedStateHandle[SendRouter.CRYPTO_CURRENCY_KEY]
        ?: error("This screen can't open without `CryptoCurrency`")

    private val selectedAppCurrencyFlow: StateFlow<AppCurrency> = createSelectedAppCurrencyFlow()

    private var innerRouter: StateRouter by Delegates.notNull()

    private val stateFactory = SendStateFactory(
        clickIntents = this,
        currentStateProvider = Provider { uiState },
        userWalletProvider = Provider { userWallet },
        walletAddressesProvider = Provider { walletAddresses },
        appCurrencyProvider = Provider(selectedAppCurrencyFlow::value),
        cryptoCurrencyStatusProvider = Provider { cryptoCurrencyStatus },
    )

    var uiState: SendUiState by mutableStateOf(stateFactory.getInitialState())
        private set

    private var userWallet: UserWallet by Delegates.notNull()
    private var cryptoCurrencyStatus: CryptoCurrencyStatus by Delegates.notNull()
    private var walletAddresses = emptySet<Address>()

    private var balanceJobHolder = JobHolder()
    private var recipientsJobHolder = JobHolder()
    private var walletAddressesJobHolder = JobHolder()
    private var feeJobHolder = JobHolder()

    override fun onCreate(owner: LifecycleOwner) {
        getWalletAddresses()
        subscribeOnCurrencyStatusUpdates(owner)
        getFee()
    }

    fun setRouter(router: StateRouter) {
        innerRouter = router
        uiState = uiState.copy(currentState = router.currentState)
    }

    private fun subscribeOnCurrencyStatusUpdates(owner: LifecycleOwner) {
        viewModelScope.launch(dispatchers.main) {
            getUserWalletUseCase(userWalletId).fold(
                ifRight = { wallet ->
                    userWallet = wallet
                    getCurrencyStatusUpdates(owner, wallet)
                },
                ifLeft = {
                    // TODO add error handling
                    return@launch
                },
            )
        }
    }

    private fun getCurrencyStatusUpdates(owner: LifecycleOwner, wallet: UserWallet) {
        val isSingleWallet = wallet.scanResponse.walletData?.token != null && !wallet.isMultiCurrency
        getCurrencyStatusUpdatesUseCase(
            userWalletId = userWalletId,
            currencyId = cryptoCurrency.id,
            isSingleWalletWithTokens = isSingleWallet,
        )
            .flowWithLifecycle(owner.lifecycle)
            .conflate()
            .distinctUntilChanged()
            .onEach { either ->
                either.onRight {
                    cryptoCurrencyStatus = it
                    getWalletsAndRecent()
                    uiState = stateFactory.getReadyState()
                }
            }
            .flowOn(dispatchers.main)
            .launchIn(viewModelScope)
            .saveIn(balanceJobHolder)
    }

    private fun createSelectedAppCurrencyFlow(): StateFlow<AppCurrency> {
        return getSelectedAppCurrencyUseCase()
            .map { maybeAppCurrency ->
                maybeAppCurrency.getOrElse { AppCurrency.Default }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = AppCurrency.Default,
            )
    }

    private fun getWalletsAndRecent() {
        combine(
            flow = getUserWallets().conflate(),
            flow2 = getTxHistory().conflate(),
            flow3 = getTxHistoryCount().conflate(),
        ) { wallets, txHistory, txHistoryCount ->
            stateFactory.onLoadedRecipientList(
                wallets = wallets,
                txHistory = txHistory,
                txHistoryCount = txHistoryCount,
            )
        }
            .flowOn(dispatchers.io)
            .launchIn(viewModelScope)
            .saveIn(recipientsJobHolder)
    }

    private fun getUserWallets(): Flow<List<AvailableWallet?>> {
        return getWalletsUseCase()
            .distinctUntilChanged()
            .map { userWallets ->
                coroutineScope {
                    userWallets
                        .filterNot { it.walletId == userWalletId || it.isLocked }
                        .map { wallet ->
                            async(dispatchers.io) {
                                getCryptoCurrenciesUseCase(wallet.walletId)
                                    .fold(
                                        ifRight = { currencyItem ->
                                            val walletCurrency = currencyItem.firstOrNull {
                                                it.network.id == cryptoCurrency.network.id
                                            } ?: return@fold null
                                            val addresses = walletManagersFacade.getAddress(
                                                userWalletId = wallet.walletId,
                                                network = walletCurrency.network,
                                            )
                                            return@fold AvailableWallet(
                                                name = wallet.name,
                                                address = addresses.first().value,
                                            )
                                        },
                                        ifLeft = { null },
                                    )
                            }
                        }
                }.awaitAll()
            }
    }

    private fun getTxHistory(): Flow<PagingData<TxHistoryItem>> {
        return flow {
            txHistoryItemsUseCase(
                userWalletId = userWalletId,
                currency = cryptoCurrency,
            ).fold(
                ifRight = { emitAll(it.distinctUntilChanged()) },
                ifLeft = {},
            )
        }
    }

    private fun getTxHistoryCount(): Flow<Int> {
        return flow {
            txHistoryItemsCountUseCase(
                userWalletId = userWalletId,
                currency = cryptoCurrency,
            ).fold(
                ifRight = { emit(it) },
                ifLeft = { emit(0) },
            )
        }
    }

    private fun getFee() {
        viewModelScope.launch(dispatchers.main) {
            uiState.currentState
                .filter { it == SendUiStateType.Fee }
                .onEach {
                    val amountState = uiState.amountState ?: return@onEach
                    val recipientState = uiState.recipientState ?: return@onEach

                    stateFactory.onFeeOnLoadingState()
                    getFeeUseCase.invoke(
                        amount = amountState.amountTextField.value.value.toBigDecimal(),
                        destination = recipientState.addressTextField.value.value,
                        userWalletId = userWalletId,
                        cryptoCurrency = cryptoCurrency,
                    )
                        .conflate()
                        .distinctUntilChanged()
                        .onEach { maybeFee ->
                            maybeFee.fold(
                                ifRight = {
                                    stateFactory.onFeeOnLoadedState(it)
                                },
                                ifLeft = {
                                    // TODO add error handling
                                },
                            )
                        }
                        .launchIn(viewModelScope)
                }.launchIn(viewModelScope)
        }.saveIn(feeJobHolder)
    }

    private fun getWalletAddresses() {
        viewModelScope.launch(dispatchers.io) {
            walletAddresses = walletManagersFacade.getAddresses(
                userWalletId = userWalletId,
                network = cryptoCurrency.network,
            )
        }.saveIn(walletAddressesJobHolder)
    }

    // region screen state navigation
    override fun onBackClick() = innerRouter.onBackClick()
    override fun onNextClick() = innerRouter.onNextClick()
    override fun onPrevClick() = innerRouter.onPrevClick()

    override fun onQrCodeScanClick() {
        // TODO Add QR code scanning
    }
    // endregion

    // region amount state clicks
    override fun onCurrencyChangeClick(isFiat: Boolean) {
        uiState = stateFactory.getOnCurrencyChangedState(isFiat)
    }

    override fun onAmountValueChange(value: String) {
        uiState = stateFactory.getOnAmountValueChange(value)
    }

    override fun onMaxValueClick() {
        val amountState = uiState.amountState ?: return
        val amount = if (amountState.isFiatValue) {
            amountState.cryptoCurrencyStatus.value.fiatAmount
        } else {
            amountState.cryptoCurrencyStatus.value.amount
        }
        onAmountValueChange(amount?.toPlainString() ?: DEFAULT_VALUE)
    }
    // endregion

    // region recipient state clicks
    override fun onRecipientAddressValueChange(value: String) {
        if (!checkIfXrpAddressValue(value)) {
            uiState = stateFactory.getOnRecipientAddressValueChangeState(value)
        }
    }

    override fun onRecipientMemoValueChange(value: String) {
        if (!checkIfXrpAddressValue(value)) {
            uiState = stateFactory.getOnRecipientMemoValueChangeState(value)
        }
    }

    private fun checkIfXrpAddressValue(value: String): Boolean {
        if (cryptoCurrency.network.id.value == Blockchain.XRP.id && value.first() == XRP_X_ADDRESS) {
            viewModelScope.launch(dispatchers.io) {
                val result = XrpAddressService.decodeXAddress(value)
                onRecipientAddressValueChange(result?.address.orEmpty())
                onRecipientMemoValueChange(result?.destinationTag.toString())
            }
            return true
        }
        return false
    }
    // endregion

    //region fee
    override fun onFeeSelectorClick(feeType: FeeType) {
        stateFactory.onFeeSelectedState(feeType)
        updateReceiveAmount()
    }

    override fun onCustomFeeValueChange(index: Int, value: String) {
        uiState.feeState?.apply {
            (feeSelectorState.value as? FeeSelectorState.Content)?.let { feeSelector ->
                feeSelector.customValues.update {
                    it.toMutableList().apply {
                        set(index, it[index].copy(value = value))
                    }
                }
                updateReceiveAmount()
            }
        }
    }

    override fun onSubtractSelect(value: Boolean) {
        uiState.feeState?.isSubtract?.update { value }
        if (value) {
            updateReceiveAmount()
        }
    }

    private fun updateReceiveAmount() {
        uiState.feeState?.receivedAmount?.update {
            BigDecimalFormatter.formatCryptoAmount(
                cryptoAmount = calculateReceiveAmount(),
                cryptoCurrency = cryptoCurrency.symbol,
                decimals = cryptoCurrency.decimals,
            )
        }
    }

    private fun calculateReceiveAmount(): BigDecimal {
        val feeState = uiState.feeState?.feeSelectorState?.value as? FeeSelectorState.Content ?: return BigDecimal.ZERO
        val amount = uiState.amountState?.amountTextField?.value ?: return BigDecimal.ZERO

        val fee = when (val selectedFee = feeState.fees) {
            is TransactionFee.Choosable -> {
                when (feeState.selectedFee) {
                    FeeType.SLOW -> selectedFee.minimum.amount.value
                    FeeType.MARKET -> selectedFee.normal.amount.value
                    FeeType.FAST -> selectedFee.priority.amount.value
                    FeeType.CUSTOM -> feeState.customValues.value.firstOrNull()?.value?.let { BigDecimal(it) }
                }
            }
            is TransactionFee.Single -> selectedFee.normal.amount.value
        } ?: BigDecimal.ZERO

        return BigDecimal(amount.value).minus(fee)
    }
    //endregion

    companion object {
        private const val XRP_X_ADDRESS = 'X'
        private const val DEFAULT_VALUE = "0.00"
    }
}
