package com.tangem.tap.features.wallet.ui.wallet.saltPay

import com.tangem.blockchain.common.TransactionData
import com.tangem.blockchain.common.TransactionStatus
import com.tangem.tap.features.wallet.models.PendingTransactionType

/**
 * Created by Anton Zhilenkov on 15.02.2023.
 */
data class HistoryTransactionData(
    val transactionData: TransactionData,
    private val walletAddress: String,
) {
    fun isInProgress(): Boolean = transactionData.status == TransactionStatus.Unconfirmed

    fun getTransactionType(): PendingTransactionType = when {
        transactionData.sourceAddress.lowercase() == walletAddress.lowercase() -> PendingTransactionType.Outgoing
        transactionData.destinationAddress.lowercase() == walletAddress.lowercase() -> PendingTransactionType.Incoming
        else -> PendingTransactionType.Unknown
    }
}
