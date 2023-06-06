package com.tangem.feature.wallet.presentation.organizetokens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import com.tangem.core.ui.components.BackgroundActionButton
import com.tangem.core.ui.components.PrimaryButton
import com.tangem.core.ui.components.SecondaryButton
import com.tangem.core.ui.res.TangemTheme
import com.tangem.feature.wallet.impl.R
import com.tangem.feature.wallet.presentation.common.WalletPreviewData
import com.tangem.feature.wallet.presentation.common.component.NetworkGroupItem
import com.tangem.feature.wallet.presentation.common.component.TokenItem
import com.tangem.feature.wallet.presentation.common.state.TokenListState

@Composable
internal fun OrganizeTokensScreen(state: OrganizeTokensStateHolder) {
    val tokensListState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopBar(state.header, tokensListState)
        },
        content = { paddingValues ->
            TokenList(
                listState = tokensListState,
                state = state.tokens,
                modifier = Modifier.padding(paddingValues),
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            Actions(state.actions)
        },
        containerColor = TangemTheme.colors.background.secondary,
    )
}

@Composable
private fun TokenList(listState: LazyListState, state: TokenListState, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = TangemTheme.dimens.spacing16),
            state = listState,
            contentPadding = PaddingValues(
                top = TangemTheme.dimens.spacing12,
                bottom = TangemTheme.dimens.spacing92,
            ),
        ) {
            when (state) {
                is TokenListState.GroupedByNetwork -> {
                    itemsIndexed(
                        items = state.groups,
                        key = { _, group -> group.id },
                    ) { index, group ->
                        NetworkGroupItem(
                            modifier = Modifier
                                .clipFirstAndLastItems(index, state.groups.lastIndex),
                            state = group,
                        )
                    }
                }
                is TokenListState.Ungrouped -> {
                    itemsIndexed(
                        items = state.tokens,
                        key = { _, token -> token.id },
                    ) { index, token ->
                        TokenItem(
                            modifier = Modifier
                                .clipFirstAndLastItems(index, state.tokens.lastIndex),
                            state = token,
                        )
                    }
                }
            }
        }

        BottomGradient(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun BottomGradient(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(TangemTheme.dimens.size116)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        TangemTheme.colors.background.secondary,
                    ),
                ),
            ),
    )
}

@Composable
private fun TopBar(
    config: OrganizeTokensStateHolder.HeaderConfig,
    tokensListState: LazyListState,
    modifier: Modifier = Modifier,
) {
    val isElevationEnabled by remember {
        derivedStateOf {
            tokensListState.firstVisibleItemScrollOffset > 0
        }
    }
    val elevation by animateDpAsState(
        targetValue = if (isElevationEnabled) TangemTheme.dimens.elevation1 else TangemTheme.dimens.elevation0,
        label = "top_bar_shadow_elevation",
    )

    Column(
        modifier = modifier
            .shadow(elevation)
            .background(TangemTheme.colors.background.secondary)
            .padding(horizontal = TangemTheme.dimens.spacing16)
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = TangemTheme.dimens.size56),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            Text(
                text = stringResource(id = R.string.organize_tokens_title),
                style = TangemTheme.typography.subtitle1,
                color = TangemTheme.colors.text.primary1,
                maxLines = 1,
            )
        }
        Row(
            modifier = Modifier
                .heightIn(min = TangemTheme.dimens.size56)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(TangemTheme.dimens.spacing8),
        ) {
            BackgroundActionButton(
                modifier = Modifier.weight(1f),
                text = stringResource(id = R.string.organize_tokens_sort_by_balance),
                iconResId = R.drawable.ic_sort_24,
                onClick = config.onSortByBalanceClick,
            )
            BackgroundActionButton(
                modifier = Modifier.weight(1f),
                text = stringResource(id = R.string.organize_tokens_group),
                iconResId = R.drawable.ic_group_24,
                onClick = config.onGroupByNetworkClick,
            )
        }
    }
}

@Composable
private fun Actions(config: OrganizeTokensStateHolder.ActionsConfig, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(horizontal = TangemTheme.dimens.spacing16)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(TangemTheme.dimens.spacing12),
    ) {
        SecondaryButton(
            modifier = Modifier.weight(1f),
            text = stringResource(id = R.string.common_cancel),
            onClick = config.onCancelClick,
        )
        PrimaryButton(
            modifier = Modifier.weight(1f),
            text = stringResource(id = R.string.common_apply),
            onClick = config.onApplyClick,
        )
    }
}

private fun Modifier.clipFirstAndLastItems(index: Int, lastItemIndex: Int): Modifier = composed {
    when (index) {
        0 -> {
            this
                .clip(
                    RoundedCornerShape(
                        topStart = TangemTheme.dimens.radius16,
                        topEnd = TangemTheme.dimens.radius16,
                    ),
                )
        }
        lastItemIndex -> {
            this
                .clip(
                    RoundedCornerShape(
                        bottomStart = TangemTheme.dimens.radius16,
                        bottomEnd = TangemTheme.dimens.radius16,
                    ),
                )
        }
        else -> this
    }
}

// region Preview

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun OrganizeTokensScreenPreview_Light(
    @PreviewParameter(OrganizeTokensStateProvider::class) state: OrganizeTokensStateHolder,
) {
    TangemTheme {
        OrganizeTokensScreen(state)
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun OrganizeTokensScreenPreview_Dark(
    @PreviewParameter(OrganizeTokensStateProvider::class) state: OrganizeTokensStateHolder,
) {
    TangemTheme(isDark = true) {
        OrganizeTokensScreen(state)
    }
}

private class OrganizeTokensStateProvider : CollectionPreviewParameterProvider<OrganizeTokensStateHolder>(
    collection = listOf(
        WalletPreviewData.organizeTokensState,
        WalletPreviewData.groupedOrganizeTokensState,
    ),
)
// endregion Preview
