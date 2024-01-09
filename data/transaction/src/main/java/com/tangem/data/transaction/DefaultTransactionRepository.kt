package com.tangem.data.transaction

import androidx.core.text.isDigitsOnly
import com.tangem.blockchain.blockchains.binance.BinanceTransactionExtras
import com.tangem.blockchain.blockchains.cosmos.CosmosTransactionExtras
import com.tangem.blockchain.blockchains.stellar.StellarMemo
import com.tangem.blockchain.blockchains.stellar.StellarTransactionExtras
import com.tangem.blockchain.blockchains.ton.TonTransactionExtras
import com.tangem.blockchain.blockchains.xrp.XrpTransactionBuilder
import com.tangem.blockchain.common.Amount
import com.tangem.blockchain.common.Blockchain
import com.tangem.blockchain.common.TransactionData
import com.tangem.blockchain.common.TransactionExtras
import com.tangem.blockchain.common.transaction.Fee
import com.tangem.domain.tokens.model.Network
import com.tangem.domain.transaction.TransactionRepository
import com.tangem.domain.walletmanager.WalletManagersFacade
import com.tangem.domain.wallets.models.UserWalletId
import com.tangem.utils.coroutines.CoroutineDispatcherProvider
import kotlinx.coroutines.withContext

internal class DefaultTransactionRepository(
    private val walletManagersFacade: WalletManagersFacade,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) : TransactionRepository {

    override suspend fun createTransaction(
        amount: Amount,
        fee: Fee,
        memo: String?,
        destination: String,
        userWalletId: UserWalletId,
        network: Network,
    ): TransactionData? = withContext(coroutineDispatcherProvider.io) {
        val blockchain = Blockchain.fromId(network.id.value)
        val walletManager = walletManagersFacade.getOrCreateWalletManager(
            userWalletId = userWalletId,
            blockchain = blockchain,
            derivationPath = network.derivationPath.value,
        )

        return@withContext walletManager?.createTransaction(amount, fee, destination)?.copy(
            extras = getMemoExtras(network.id.value, memo),
        )
    }

    private fun getMemoExtras(networkId: String, memo: String?): TransactionExtras? {
        val blockchain = Blockchain.fromId(networkId)
        if (memo == null) return null
        return when (blockchain) {
            Blockchain.Stellar -> {
                val xmlMemo = when {
                    memo.isNotEmpty() && memo.isDigitsOnly() -> StellarMemo.Id(memo.toBigInteger())
                    else -> StellarMemo.Text(memo)
                }
                StellarTransactionExtras(xmlMemo)
            }
            Blockchain.Binance -> BinanceTransactionExtras(memo)
            Blockchain.XRP -> memo.toLongOrNull()?.let { XrpTransactionBuilder.XrpTransactionExtras(it) }
            Blockchain.Cosmos -> CosmosTransactionExtras(memo)
            Blockchain.TON -> TonTransactionExtras(memo)
            else -> null
        }
    }
}