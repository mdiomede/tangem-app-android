package com.tangem.lib.crypto.models

import java.math.BigDecimal

/**
 * Tx data for create and make transaction
 *
 * @property networkId id of network
 * @property amountToSend amount of tx
 * @property feeAmount amount of fee
 * @property gasLimit gasLimit for given tx
 * @property destinationAddress address to send tx
 * @property dataToSign data to sing with signer
 * @property currencyToSend currency for tx
 */
data class TxData(
    val networkId: String,
    val amountToSend: BigDecimal,
    val feeAmount: BigDecimal,
    val gasLimit: Int,
    val destinationAddress: String,
    val dataToSign: String,
    val currencyToSend: Currency,
)