package com.tangem.data.tokens.repository

import com.tangem.data.common.cache.CacheRegistry
import com.tangem.data.tokens.utils.QuotesConverter
import com.tangem.datasource.api.tangemTech.TangemTechApi
import com.tangem.datasource.local.quote.QuotesStore
import com.tangem.domain.tokens.models.CryptoCurrency
import com.tangem.domain.tokens.models.Quote
import com.tangem.domain.tokens.repository.QuotesRepository
import com.tangem.utils.coroutines.CoroutineDispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

internal class DefaultQuotesRepository(
    private val tangemTechApi: TangemTechApi,
    private val quotesStore: QuotesStore,
    private val cacheRegistry: CacheRegistry,
    private val dispatchers: CoroutineDispatcherProvider,
) : QuotesRepository {

    private val quotesConverter = QuotesConverter()

    override fun getQuotes(currenciesIds: Set<CryptoCurrency.ID>, refresh: Boolean): Flow<Set<Quote>> {
        return channelFlow {
            launch(dispatchers.io) {
                quotesStore.get(currenciesIds)
                    .map(quotesConverter::convertSet)
                    .collect(::send)
            }

            launch(dispatchers.io) {
                fetchExpiredQuotes(currenciesIds, refresh)
            }
        }
    }

    private suspend fun fetchExpiredQuotes(currenciesIds: Set<CryptoCurrency.ID>, refresh: Boolean) {
        val expiredCurrenciesIds = filterExpiredCurrenciesIds(currenciesIds, refresh)
        if (expiredCurrenciesIds.isEmpty()) return

        fetchQuotes(expiredCurrenciesIds)
    }

    private suspend fun fetchQuotes(rawCurrenciesIds: Set<String>) {
        try {
            val response = tangemTechApi.getQuotes(
                currencyId = "usd", // TODO: https://tangem.atlassian.net/browse/AND-4006
                coinIds = rawCurrenciesIds.joinToString(separator = ","),
            )

            quotesStore.store(response)
        } catch (e: Throwable) {
            Timber.e(e, "Unable to fetch quotes for: $rawCurrenciesIds")
            throw e
        }
    }

    private suspend fun filterExpiredCurrenciesIds(
        currenciesIds: Set<CryptoCurrency.ID>,
        refresh: Boolean,
    ): Set<String> {
        return currenciesIds.fold(hashSetOf()) { acc, currencyId ->
            val rawCurrencyId = currencyId.rawCurrencyId

            if (rawCurrencyId != null && rawCurrencyId !in acc) {
                cacheRegistry.invokeOnExpire(
                    key = getQuoteCacheKey(rawCurrencyId),
                    skipCache = refresh,
                    block = { acc.add(rawCurrencyId) },
                )
            }

            acc
        }
    }

    private fun getQuoteCacheKey(rawCurrencyId: String): String = "quote_$rawCurrencyId"
}