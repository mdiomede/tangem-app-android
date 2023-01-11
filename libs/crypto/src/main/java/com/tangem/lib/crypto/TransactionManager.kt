package com.tangem.lib.crypto

import com.tangem.lib.crypto.models.Currency
import com.tangem.lib.crypto.models.ProxyAmount
import com.tangem.lib.crypto.models.transactions.SendTxResult
import java.math.BigDecimal

interface TransactionManager {

    @Throws(IllegalStateException::class)
    suspend fun sendTransaction(
        networkId: String,
        amountToSend: BigDecimal,
        currencyToSend: Currency,
        feeAmount: BigDecimal,
        destinationAddress: String,
        dataToSign: String,
    ): SendTxResult

    @Throws(IllegalStateException::class)
    suspend fun getFee(
        networkId: String,
        amountToSend: BigDecimal,
        currencyToSend: Currency,
        destinationAddress: String,
    ): ProxyAmount

    fun getNativeAddress(networkId: String): String

    @Throws(IllegalStateException::class)
    fun getNativeTokenDecimals(networkId: String): Int
}
