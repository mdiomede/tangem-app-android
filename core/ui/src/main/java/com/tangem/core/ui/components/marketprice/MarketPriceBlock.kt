package com.tangem.core.ui.components.marketprice

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.Dp
import com.tangem.core.ui.R
import com.tangem.core.ui.components.RectangleShimmer
import com.tangem.core.ui.res.TangemTheme
import com.tangem.core.ui.utils.BigDecimalFormatter

/**
 * Market price block
 *
 * @param state    component state
 * @param modifier modifier
 *
 * @see <a href = "https://www.figma.com/file/14ISV23YB1yVW1uNVwqrKv/Android?type=design&node-id=1217-922&mode
 * =design&t=t1PxyisyGJSwzQwg-4"
 * >Figma component</a>
 */
@Composable
fun MarketPriceBlock(state: MarketPriceBlockState, modifier: Modifier = Modifier) {
    var rootWidth by remember { mutableStateOf(value = 0) }

    Column(
        modifier = modifier
            .background(
                color = TangemTheme.colors.background.primary,
                shape = RoundedCornerShape(TangemTheme.dimens.radius14),
            )
            .heightIn(min = TangemTheme.dimens.size70)
            .fillMaxWidth()
            .padding(all = TangemTheme.dimens.spacing14)
            .onSizeChanged { rootWidth = it.width },
        verticalArrangement = Arrangement.spacedBy(TangemTheme.dimens.spacing6),
        horizontalAlignment = Alignment.Start,
    ) {
        Title(currencyName = state.currencySymbol)

        Content(state = state, rootWidth = rootWidth)
    }
}

@Composable
private fun Title(currencyName: String) {
    Text(
        text = stringResource(id = R.string.wallet_marketplace_block_title, currencyName),
        color = TangemTheme.colors.text.tertiary,
        style = TangemTheme.typography.subtitle2,
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun Content(state: MarketPriceBlockState, rootWidth: Int) {
    AnimatedContent(targetState = state, label = "Update the content") { marketPriceBlockState ->
        when (marketPriceBlockState) {
            is MarketPriceBlockState.Content,
            is MarketPriceBlockState.Error,
            -> {
                PriceContent(
                    state = marketPriceBlockState,
                    priceWidthDp = with(LocalDensity.current) { rootWidth.div(other = 2).toDp() },
                )
            }
            is MarketPriceBlockState.Loading -> LoadingContent()
        }
    }
}

@Composable
private fun PriceContent(state: MarketPriceBlockState, priceWidthDp: Dp) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space = TangemTheme.dimens.spacing8),
    ) {
        PriceBlock(state = state, priceWidthDp = priceWidthDp)

        QuoteTimeStatus()
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun PriceBlock(state: MarketPriceBlockState, priceWidthDp: Dp) {
    val priceModifier = Modifier.widthIn(max = priceWidthDp)
    AnimatedContent(targetState = state, label = "Update the price block") { marketPriceBlockState ->
        if (marketPriceBlockState is MarketPriceBlockState.Content) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(space = TangemTheme.dimens.spacing8),
            ) {
                Price(price = marketPriceBlockState.price, modifier = priceModifier)

                PriceChangeInPercent(marketPriceBlockState.priceChangeConfig)
            }
        } else {
            Price(price = BigDecimalFormatter.EMPTY_BALANCE_SIGN, modifier = priceModifier)
        }
    }
}

@Composable
private fun Price(price: String, modifier: Modifier = Modifier) {
    Text(
        text = price,
        modifier = modifier,
        color = TangemTheme.colors.text.primary1,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        style = TangemTheme.typography.body2,
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun PriceChangeInPercent(config: PriceChangeState.Content) {
    AnimatedContent(targetState = config.type, label = "Update price change") { type ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(space = TangemTheme.dimens.spacing4),
        ) {
            Icon(
                painter = painterResource(
                    id = when (type) {
                        PriceChangeType.UP -> R.drawable.ic_arrow_up_8
                        PriceChangeType.DOWN -> R.drawable.ic_arrow_down_8
                    },
                ),
                tint = when (type) {
                    PriceChangeType.UP -> TangemTheme.colors.icon.accent
                    PriceChangeType.DOWN -> TangemTheme.colors.icon.warning
                },
                contentDescription = null,
            )

            Text(
                text = config.valueInPercent,
                color = when (type) {
                    PriceChangeType.UP -> TangemTheme.colors.text.accent
                    PriceChangeType.DOWN -> TangemTheme.colors.text.warning
                },
                style = TangemTheme.typography.body2,
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space = TangemTheme.dimens.spacing8),
    ) {
        RectangleShimmer(
            modifier = Modifier.size(
                width = TangemTheme.dimens.size158,
                height = TangemTheme.dimens.size18,
            ),
        )

        QuoteTimeStatus()
    }
}

@Composable
private fun QuoteTimeStatus() {
    Text(
        text = stringResource(id = R.string.wallet_marketprice_block_update_time),
        color = TangemTheme.colors.text.tertiary,
        style = TangemTheme.typography.body2,
    )
}

@Preview
@Composable
private fun Preview_MarketPriceBlock_Light(
    @PreviewParameter(WalletMarketPriceBlockStateProvider::class)
    state: MarketPriceBlockState,
) {
    TangemTheme(isDark = false) {
        MarketPriceBlock(state = state)
    }
}

@Preview
@Composable
private fun Preview_MarketPriceBlock_Dark(
    @PreviewParameter(WalletMarketPriceBlockStateProvider::class)
    state: MarketPriceBlockState,
) {
    TangemTheme(isDark = true) {
        MarketPriceBlock(state = state)
    }
}

private class WalletMarketPriceBlockStateProvider : CollectionPreviewParameterProvider<MarketPriceBlockState>(
    collection = listOf(
        MarketPriceBlockState.Content(
            currencySymbol = "BTC",
            price = "98900 $",
            priceChangeConfig = PriceChangeState.Content(
                valueInPercent = "5.16%",
                type = PriceChangeType.DOWN,
            ),
        ),
        MarketPriceBlockState.Content(
            currencySymbol = "BTC",
            price = "98900 $",
            priceChangeConfig = PriceChangeState.Content(
                valueInPercent = "10.89%",
                type = PriceChangeType.UP,
            ),
        ),
        MarketPriceBlockState.Loading(currencySymbol = "BTC"),
        MarketPriceBlockState.Error(currencySymbol = "BTC"),
    ),
)
