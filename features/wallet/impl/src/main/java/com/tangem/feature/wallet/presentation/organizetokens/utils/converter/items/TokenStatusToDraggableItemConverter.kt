package com.tangem.feature.wallet.presentation.organizetokens.utils.converter.items

import androidx.annotation.DrawableRes
import com.tangem.core.ui.utils.BigDecimalFormatter
import com.tangem.domain.tokens.model.TokenStatus
import com.tangem.feature.wallet.impl.R
import com.tangem.feature.wallet.presentation.common.state.TokenItemState
import com.tangem.feature.wallet.presentation.organizetokens.DraggableItem
import com.tangem.feature.wallet.presentation.organizetokens.utils.common.getGroupHeaderId
import com.tangem.feature.wallet.presentation.organizetokens.utils.common.getTokenItemId
import com.tangem.utils.converter.Converter

internal class TokenStatusToDraggableItemConverter(
    private val fiatCurrencyCode: String,
    private val fiatCurrencySymbol: String,
) : Converter<TokenStatus, DraggableItem.Token> {

    private val TokenStatus.networkIconResId: Int?
        @DrawableRes get() {
            // TODO: https://tangem.atlassian.net/browse/AND-4009
            return if (isCoin) null else R.drawable.img_eth_22
        }

    private val TokenStatus.tokenIconResId: Int
        @DrawableRes get() {
            // TODO: https://tangem.atlassian.net/browse/AND-4009
            return R.drawable.img_eth_22
        }

    override fun convert(value: TokenStatus): DraggableItem.Token {
        return DraggableItem.Token(
            tokenItemState = createToTokenItemState(value),
            groupId = getGroupHeaderId(value.networkId),
        )
    }

    private fun createToTokenItemState(token: TokenStatus) = TokenItemState.Draggable(
        id = getTokenItemId(token.id),
        tokenIconUrl = token.iconUrl,
        tokenIconResId = token.tokenIconResId,
        networkIconResId = token.networkIconResId,
        name = token.name,
        fiatAmount = getFormattedFiatAmount(token),
    )

    private fun getFormattedFiatAmount(token: TokenStatus): String {
        val fiatAmount = token.value.fiatAmount ?: return BigDecimalFormatter.EMPTY_BALANCE_SIGN

        return BigDecimalFormatter.formatFiatAmount(fiatAmount, fiatCurrencyCode, fiatCurrencySymbol)
    }
}
