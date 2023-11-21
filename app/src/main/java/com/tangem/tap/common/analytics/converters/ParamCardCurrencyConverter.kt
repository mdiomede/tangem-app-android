package com.tangem.tap.common.analytics.converters

import com.tangem.blockchain.common.Blockchain
import com.tangem.domain.common.CardTypesResolver
import com.tangem.tap.common.analytics.events.AnalyticsParam
import com.tangem.utils.converter.Converter

/**
 * Created by Anton Zhilenkov on 02.11.2022.
 */
class ParamCardCurrencyConverter : Converter<CardTypesResolver, AnalyticsParam.CardCurrency?> {

    override fun convert(value: CardTypesResolver): AnalyticsParam.CardCurrency? {
        if (value.isMultiwalletAllowed()) return AnalyticsParam.CardCurrency.MultiCurrency

        val type = when {
            value.isTangemNote() -> AnalyticsParam.CurrencyType.Blockchain(value.getBlockchain())
            value.isTangemTwins() -> AnalyticsParam.CurrencyType.Blockchain(Blockchain.Bitcoin)
            value.getBlockchain() != Blockchain.Unknown -> AnalyticsParam.CurrencyType.Blockchain(value.getBlockchain())
            value.getPrimaryToken() != null -> AnalyticsParam.CurrencyType.Token(value.getPrimaryToken()!!)
            else -> null
        } ?: return null

        return AnalyticsParam.CardCurrency.SingleCurrency(type)
    }
}
