package com.tangem.features.send.impl.presentation.state

import com.tangem.blockchain.common.Blockchain
import com.tangem.core.ui.utils.BigDecimalFormatter
import com.tangem.domain.tokens.model.CryptoCurrency
import com.tangem.domain.tokens.model.CryptoCurrencyStatus
import com.tangem.domain.walletmanager.WalletManagersFacade
import com.tangem.domain.wallets.models.UserWallet
import com.tangem.utils.Provider
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.math.BigDecimal

internal class SendNotificationFactory(
    private val cryptoCurrencyStatusProvider: Provider<CryptoCurrencyStatus>,
    private val coinCryptoCurrencyStatusProvider: Provider<CryptoCurrencyStatus>,
    private val currentStateProvider: Provider<SendUiState>,
    private val userWalletProvider: Provider<UserWallet>,
    private val walletManagersFacade: WalletManagersFacade,
) {

    fun create(): Flow<ImmutableList<SendNotification>> = currentStateProvider().currentState
        .filter { it == SendUiStateType.Send }
        .map {
            val state = currentStateProvider()
            val feeState = state.feeState ?: return@map persistentListOf()
            val recipientState = state.recipientState ?: return@map persistentListOf()
            val feeAmount = feeState.fee?.amount?.value ?: BigDecimal.ZERO
            buildList {
                // errors
                addExceedBalanceNotification(feeAmount, feeState.receivedAmountValue)
                addInvalidAmountNotification(feeState.isSubtract, feeState.receivedAmountValue)
                addMinimumAmountErrorNotification(feeAmount, feeState.receivedAmountValue)
                addReserveAmountErrorNotification(recipientState.addressTextField.value)
                addTransactionLimitErrorNotification(feeAmount, feeState.receivedAmountValue)
                // warnings
                addExistentialWarningNotification(feeAmount, feeState.receivedAmountValue)
                addHighFeeWarningNotification()
            }.toImmutableList()
        }

    private fun MutableList<SendNotification>.addExceedBalanceNotification(
        feeAmount: BigDecimal,
        receivedAmount: BigDecimal,
    ) {
        val cryptoCurrencyStatus = cryptoCurrencyStatusProvider()
        val coinCryptoCurrencyStatus = coinCryptoCurrencyStatusProvider()
        val cryptoAmount = cryptoCurrencyStatus.value.amount ?: BigDecimal.ZERO
        val coinCryptoAmount = coinCryptoCurrencyStatus.value.amount ?: BigDecimal.ZERO

        val showNotification = if (cryptoCurrencyStatus.currency is CryptoCurrency.Token) {
            receivedAmount > cryptoAmount || feeAmount > coinCryptoAmount
        } else {
            receivedAmount + feeAmount > cryptoAmount
        }

        if (showNotification) {
            add(SendNotification.Error.TotalExceedsBalance)
        }
    }

    private fun MutableList<SendNotification>.addInvalidAmountNotification(
        isSubtractAmount: Boolean,
        receivedAmount: BigDecimal,
    ) {
        if (isSubtractAmount && receivedAmount <= BigDecimal.ZERO) {
            add(SendNotification.Error.InvalidAmount)
        }
    }

    private fun MutableList<SendNotification>.addMinimumAmountErrorNotification(
        feeAmount: BigDecimal,
        receivedAmount: BigDecimal,
    ) {
        val coinCryptoCurrencyStatus = coinCryptoCurrencyStatusProvider()

        val totalAmount = feeAmount + receivedAmount
        val balance = coinCryptoCurrencyStatus.value.amount ?: BigDecimal.ZERO

        // TODO Move Blockchain check elsewhere
        when (coinCryptoCurrencyStatus.currency.network.id.value) {
            Blockchain.Cardano.id -> {
                if (receivedAmount > BigDecimal.ONE || balance - totalAmount < BigDecimal.ONE) {
                    add(SendNotification.Error.MinimumAmountError(CARDANO_MINIMUM))
                }
            }
            Blockchain.Dogecoin.id -> {
                val minimum = BigDecimal(DOGECOIN_MINIMUM)
                if (receivedAmount > minimum || balance - totalAmount < minimum) {
                    add(SendNotification.Error.MinimumAmountError(DOGECOIN_MINIMUM))
                }
            }
            else -> Unit
        }
    }

    private suspend fun MutableList<SendNotification>.addReserveAmountErrorNotification(recipientAddress: String) {
        val userWalletId = userWalletProvider().walletId
        val cryptoCurrency = cryptoCurrencyStatusProvider().currency
        val isAccountFunded = walletManagersFacade.checkIfAccountFunded(
            userWalletId,
            cryptoCurrency.network,
            recipientAddress,
        )
        val minimumAmount = walletManagersFacade.getReserveAmount(userWalletId, cryptoCurrency.network)
        if (!isAccountFunded && minimumAmount != null && minimumAmount > BigDecimal.ZERO) {
            add(
                SendNotification.Error.ReserveAmountError(
                    BigDecimalFormatter.formatCryptoAmount(
                        cryptoAmount = minimumAmount,
                        cryptoCurrency = cryptoCurrency,
                    ),
                ),
            )
        }
    }

    private suspend fun MutableList<SendNotification>.addTransactionLimitErrorNotification(
        feeAmount: BigDecimal,
        receivedAmount: BigDecimal,
    ) {
        val userWalletId = userWalletProvider().walletId
        val cryptoCurrency = cryptoCurrencyStatusProvider().currency
        val utxoLimit = walletManagersFacade.checkUtxoAmountLimit(
            userWalletId = userWalletId,
            network = cryptoCurrency.network,
            amount = receivedAmount,
            fee = feeAmount,
        )

        if (utxoLimit != null) {
            add(
                SendNotification.Error.TransactionLimitError(
                    cryptoCurrency = cryptoCurrency.name,
                    utxoLimit = utxoLimit.maxLimit.toPlainString(),
                    amountLimit = BigDecimalFormatter.formatCryptoAmount(
                        cryptoAmount = utxoLimit.maxAmount,
                        cryptoCurrency = cryptoCurrency,
                    ),
                ),
            )
        }
    }

    private suspend fun MutableList<SendNotification>.addExistentialWarningNotification(
        feeAmount: BigDecimal,
        receivedAmount: BigDecimal,
    ) {
        val userWalletId = userWalletProvider().walletId
        val cryptoCurrency = cryptoCurrencyStatusProvider().currency
        val spendingAmount = if (cryptoCurrency is CryptoCurrency.Token) {
            feeAmount
        } else {
            feeAmount + receivedAmount
        }
        val currencyDeposit = walletManagersFacade.getExistentialDeposit(
            userWalletId,
            cryptoCurrency.network,
        )
        if (currencyDeposit != null && currencyDeposit > spendingAmount) {
            add(
                SendNotification.Warning.ExistentialDeposit(
                    BigDecimalFormatter.formatCryptoAmount(
                        cryptoAmount = currencyDeposit,
                        cryptoCurrency = cryptoCurrency,
                    ),
                ),
            )
        }
    }

    private fun MutableList<SendNotification>.addHighFeeWarningNotification() {
        // TODO Move Blockchain check elsewhere
        if (cryptoCurrencyStatusProvider().currency.network.id.value == Blockchain.Tezos.id) {
            add(SendNotification.Warning.HighFeeError(TEZOS_FEE_THRESHOLD))
        }
    }

    companion object {
        private const val CARDANO_MINIMUM = "1"
        private const val DOGECOIN_MINIMUM = "0.01"
        private const val TEZOS_FEE_THRESHOLD = "0.01"
    }
}