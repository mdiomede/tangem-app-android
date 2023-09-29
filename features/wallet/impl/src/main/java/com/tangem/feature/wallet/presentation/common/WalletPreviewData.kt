package com.tangem.feature.wallet.presentation.common

import androidx.paging.PagingData
import com.tangem.core.ui.R
import com.tangem.core.ui.components.bottomsheets.TangemBottomSheetConfig
import com.tangem.core.ui.components.marketprice.MarketPriceBlockState
import com.tangem.core.ui.components.marketprice.PriceChangeState
import com.tangem.core.ui.components.marketprice.PriceChangeType
import com.tangem.core.ui.components.transactions.state.TransactionState
import com.tangem.core.ui.components.transactions.state.TxHistoryState
import com.tangem.core.ui.event.consumedEvent
import com.tangem.core.ui.extensions.TextReference
import com.tangem.core.ui.extensions.stringReference
import com.tangem.core.ui.res.TangemColorPalette
import com.tangem.domain.wallets.models.UserWalletId
import com.tangem.feature.wallet.presentation.common.state.TokenItemState
import com.tangem.feature.wallet.presentation.common.state.TokenItemState.TokenOptionsState
import com.tangem.feature.wallet.presentation.organizetokens.model.DraggableItem
import com.tangem.feature.wallet.presentation.organizetokens.model.OrganizeTokensListState
import com.tangem.feature.wallet.presentation.organizetokens.model.OrganizeTokensState
import com.tangem.feature.wallet.presentation.wallet.state.ActionsBottomSheetConfig
import com.tangem.feature.wallet.presentation.wallet.state.TokenActionButtonConfig
import com.tangem.feature.wallet.presentation.wallet.state.WalletMultiCurrencyState
import com.tangem.feature.wallet.presentation.wallet.state.WalletSingleCurrencyState
import com.tangem.feature.wallet.presentation.wallet.state.components.*
import com.tangem.feature.wallet.presentation.wallet.state.components.WalletTokensListState.TokensListItemState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

@Suppress("LargeClass")
internal object WalletPreviewData {

    val topBarConfig by lazy { WalletTopBarConfig(onDetailsClick = {}) }

    val walletCardContentState by lazy {
        WalletCardState.Content(
            id = UserWalletId(stringValue = "123"),
            title = "Wallet 1",
            balance = "8923,05 $",
            additionalInfo = TextReference.Str("3 cards • Seed phrase"),
            imageResId = R.drawable.ill_businessman_3d,
            onRenameClick = { _, _ -> },
            onDeleteClick = {},
        )
    }

    val walletCardLoadingState by lazy {
        WalletCardState.Loading(
            id = UserWalletId("321"),
            title = "Wallet 1",
            imageResId = R.drawable.ill_businessman_3d,
            onRenameClick = { _, _ -> },
            onDeleteClick = {},
        )
    }

    val walletCardHiddenContentState by lazy {
        WalletCardState.HiddenContent(
            id = UserWalletId("42"),
            title = "Wallet 1",
            imageResId = R.drawable.ill_businessman_3d,
            onRenameClick = { _, _ -> },
            onDeleteClick = {},
            balance = "8923,05 $",
            additionalInfo = TextReference.Str("3 cards • Seed phrase"),
        )
    }

    val walletCardErrorState by lazy {
        WalletCardState.Error(
            id = UserWalletId("24"),
            title = "Wallet 1",
            imageResId = R.drawable.ill_businessman_3d,
            onRenameClick = { _, _ -> },
            onDeleteClick = {},
        )
    }

    val wallets by lazy {
        mapOf(
            UserWalletId(stringValue = "123") to walletCardContentState,
            UserWalletId(stringValue = "321") to walletCardLoadingState,
            UserWalletId(stringValue = "42") to walletCardHiddenContentState,
            UserWalletId(stringValue = "24") to walletCardErrorState,
        )
    }

    val walletListConfig by lazy {
        WalletsListConfig(
            selectedWalletIndex = 0,
            wallets = wallets.values.toPersistentList(),
            onWalletChange = {},
        )
    }

    private val coinIconState
        get() = TokenItemState.IconState.CoinIcon(
            url = null,
            fallbackResId = R.drawable.img_polygon_22,
            isGrayscale = false,
            isCustom = false,
        )

    private val tokenIconState
        get() = TokenItemState.IconState.TokenIcon(
            url = null,
            networkBadgeIconResId = R.drawable.img_polygon_22,
            fallbackTint = TangemColorPalette.Black,
            fallbackBackground = TangemColorPalette.Meadow,
            isGrayscale = false,
        )

    private val customTokenIconState
        get() = TokenItemState.IconState.CustomTokenIcon(
            tint = TangemColorPalette.Black,
            background = TangemColorPalette.Meadow,
            networkBadgeIconResId = R.drawable.img_polygon_22,
            isGrayscale = false,
        )

    val tokenItemVisibleState by lazy {
        TokenItemState.Content(
            id = UUID.randomUUID().toString(),
            icon = coinIconState,
            name = "Polygon",
            amount = "5,412 MATIC",
            hasPending = true,
            tokenOptions = TokenOptionsState(
                fiatAmount = "321 $",
                priceChangeState = PriceChangeState.Unknown,
                isBalanceHidden = false,
            ),
            onItemClick = {},
            onItemLongClick = {},
        )
    }

    val testnetTokenItemVisibleState by lazy {
        tokenItemVisibleState.copy(
            name = "Polygon testnet",
            icon = tokenIconState.copy(isGrayscale = true),
        )
    }

    val tokenItemHiddenState by lazy {
        TokenItemState.Content(
            id = UUID.randomUUID().toString(),
            icon = tokenIconState,
            name = "Polygon",
            amount = "5,412 MATIC",
            hasPending = false,
            tokenOptions = TokenOptionsState(
                priceChangeState = PriceChangeState.Content(
                    valueInPercent = "2%",
                    type = PriceChangeType.UP,
                ),
                fiatAmount = "321 $",
                isBalanceHidden = false,
            ),
            onItemClick = {},
            onItemLongClick = {},
        )
    }

    val tokenItemDragState by lazy {
        TokenItemState.Draggable(
            id = UUID.randomUUID().toString(),
            icon = tokenIconState,
            name = "Polygon",
            info = stringReference(value = "3 172,14 $"),
        )
    }

    val tokenItemUnreachableState by lazy {
        TokenItemState.Unreachable(
            id = UUID.randomUUID().toString(),
            icon = tokenIconState,
            name = "Polygon",
            onItemClick = {},
            onItemLongClick = {},
        )
    }

    val tokenItemNoAddressState by lazy {
        TokenItemState.NoAddress(
            id = UUID.randomUUID().toString(),
            icon = tokenIconState,
            name = "Polygon",
            onItemLongClick = {},
        )
    }

    val customTokenItemVisibleState by lazy {
        tokenItemVisibleState.copy(
            name = "Polygon custom",
            icon = customTokenIconState.copy(
                tint = TangemColorPalette.White,
                background = TangemColorPalette.Black,
            ),
        )
    }

    val customTestnetTokenItemVisibleState by lazy {
        tokenItemVisibleState.copy(
            name = "Polygon custom testnet",
            icon = customTokenIconState.copy(isGrayscale = true),
        )
    }

    val loadingTokenItemState by lazy { TokenItemState.Loading(id = "Loading#1") }

    private const val networksSize = 10
    private const val tokensSize = 3
    private val draggableItems by lazy {
        List(networksSize) { it }
            .flatMap { index ->
                val lastNetworkIndex = networksSize - 1
                val lastTokenIndex = tokensSize - 1
                val networkNumber = index + 1

                val group = DraggableItem.GroupHeader(
                    id = networkNumber,
                    networkName = "$networkNumber",
                    roundingMode = when (index) {
                        0 -> DraggableItem.RoundingMode.Top()
                        lastNetworkIndex -> DraggableItem.RoundingMode.Bottom()
                        else -> DraggableItem.RoundingMode.None
                    },
                )

                val tokens: MutableList<DraggableItem.Token> = mutableListOf()
                repeat(times = tokensSize) { i ->
                    val tokenNumber = i + 1
                    tokens.add(
                        DraggableItem.Token(
                            tokenItemState = tokenItemDragState.copy(
                                id = "${group.id}_token_$tokenNumber",
                                name = "Token $tokenNumber from $networkNumber network",
                            ),
                            groupId = group.id,
                            roundingMode = when {
                                i == lastTokenIndex && index == lastNetworkIndex -> DraggableItem.RoundingMode.Bottom()
                                else -> DraggableItem.RoundingMode.None
                            },
                        ),
                    )
                }

                val divider = DraggableItem.Placeholder(id = "divider_$networkNumber")

                buildList {
                    add(group)
                    addAll(tokens)
                    if (index != lastNetworkIndex) {
                        add(divider)
                    }
                }
            }
            .toPersistentList()
    }

    private val draggableTokens by lazy {
        draggableItems
            .filterIsInstance<DraggableItem.Token>()
            .toMutableList()
            .also {
                it[0] = it[0].copy(roundingMode = DraggableItem.RoundingMode.Top())
            }
            .toPersistentList()
    }

    val groupedOrganizeTokensState by lazy {
        OrganizeTokensState(
            onBackClick = {},
            itemsState = OrganizeTokensListState.GroupedByNetwork(
                items = draggableItems,
            ),
            header = OrganizeTokensState.HeaderConfig(
                onSortClick = {},
                onGroupClick = {},
            ),
            dndConfig = OrganizeTokensState.DragAndDropConfig(
                onItemDragged = { _, _ -> },
                onItemDragStart = {},
                canDragItemOver = { _, _ -> false },
                onItemDragEnd = {},
            ),
            actions = OrganizeTokensState.ActionsConfig(
                onApplyClick = {},
                onCancelClick = {},
            ),
            scrollListToTop = consumedEvent(),
        )
    }

    val organizeTokensState by lazy {
        groupedOrganizeTokensState.copy(
            itemsState = OrganizeTokensListState.Ungrouped(
                items = draggableTokens,
            ),
        )
    }

    val bottomSheet by lazy {
        TangemBottomSheetConfig(
            isShow = false,
            onDismissRequest = {},
            content = WalletBottomSheetConfig.UnlockWallets(
                onUnlockClick = {},
                onScanClick = {},
            ),
        )
    }

    val actionsBottomSheet = ActionsBottomSheetConfig(
        isShow = true,
        onDismissRequest = {},
        actions = listOf(
            TokenActionButtonConfig(
                text = TextReference.Str("Send"),
                iconResId = R.drawable.ic_share_24,
                onClick = {},
            ),
        ).toImmutableList(),
    )

    private val manageButtons by lazy {
        persistentListOf(
            WalletManageButton.Buy(enabled = true, onClick = {}),
            WalletManageButton.Send(enabled = true, onClick = {}),
            WalletManageButton.Receive(onClick = {}),
            WalletManageButton.Sell(enabled = true, onClick = {}),
            WalletManageButton.Swap(enabled = true, onClick = {}),
        )
    }

    val multicurrencyWalletScreenState by lazy {
        WalletMultiCurrencyState.Content(
            onBackClick = {},
            topBarConfig = topBarConfig,
            walletsListConfig = walletListConfig,
            tokensListState = WalletTokensListState.Content(
                persistentListOf(
                    TokensListItemState.NetworkGroupTitle(id = 0, stringReference("Bitcoin")),
                    TokensListItemState.Token(
                        tokenItemVisibleState.copy(
                            id = "token_1",
                            name = "Ethereum",
                            amount = "1,89340821 ETH",
                        ),
                    ),
                    TokensListItemState.Token(
                        tokenItemVisibleState.copy(
                            id = "token_2",
                            name = "Ethereum",
                            amount = "1,89340821 ETH",
                        ),
                    ),
                    TokensListItemState.Token(
                        tokenItemVisibleState.copy(
                            id = "token_3",
                            name = "Ethereum",
                            amount = "1,89340821 ETH",
                        ),
                    ),
                    TokensListItemState.Token(
                        tokenItemVisibleState.copy(
                            id = "token_4",
                            name = "Ethereum",
                            amount = "1,89340821 ETH",
                        ),
                    ),
                    TokensListItemState.NetworkGroupTitle(id = 1, stringReference("Ethereum")),
                    TokensListItemState.Token(
                        tokenItemVisibleState.copy(
                            id = "token_5",
                            name = "Ethereum",
                            amount = "1,89340821 ETH",
                        ),
                    ),
                ),
                organizeTokensButton = WalletTokensListState.OrganizeTokensButtonState.Visible(isEnabled = true, {}),
            ),
            pullToRefreshConfig = WalletPullToRefreshConfig(
                isRefreshing = false,
                onRefresh = {},
            ),
            notifications = persistentListOf(
                WalletNotification.Critical.DevCard,
                WalletNotification.MissingAddresses(missingAddressesCount = 0, onGenerateClick = {}),
                WalletNotification.Warning.NetworksUnreachable,
            ),
            bottomSheetConfig = bottomSheet,
            tokenActionsBottomSheet = actionsBottomSheet,
            onManageTokensClick = {},
            event = consumedEvent(),
            isBalanceHidden = false,
        )
    }

    val singleWalletScreenState by lazy {
        WalletSingleCurrencyState.Content(
            onBackClick = {},
            topBarConfig = topBarConfig,
            walletsListConfig = walletListConfig,
            pullToRefreshConfig = WalletPullToRefreshConfig(
                isRefreshing = false,
                onRefresh = {},
            ),
            notifications = persistentListOf(WalletNotification.Warning.NetworksUnreachable),
            buttons = manageButtons,
            bottomSheetConfig = bottomSheet,
            marketPriceBlockState = MarketPriceBlockState.Content(
                currencySymbol = "BTC",
                price = "98900.12$",
                priceChangeConfig = PriceChangeState.Content(
                    valueInPercent = "5.16%",
                    type = PriceChangeType.UP,
                ),
            ),
            txHistoryState = TxHistoryState.Content(
                contentItems = MutableStateFlow(
                    PagingData.from(
                        listOf(
                            TxHistoryState.TxHistoryItemState.GroupTitle("Today"),
                            TxHistoryState.TxHistoryItemState.Transaction(
                                TransactionState.Transfer(
                                    txHash = UUID.randomUUID().toString(),
                                    address = TextReference.Str("33BddS...ga2B"),
                                    amount = "-0.500913 BTC",
                                    timestamp = "8:41",
                                    status = TransactionState.Content.Status.Unconfirmed,
                                    direction = TransactionState.Content.Direction.OUTGOING,
                                ),
                            ),
                            TxHistoryState.TxHistoryItemState.GroupTitle("Yesterday"),
                            TxHistoryState.TxHistoryItemState.Transaction(
                                TransactionState.Transfer(
                                    txHash = UUID.randomUUID().toString(),
                                    address = TextReference.Str("33BddS...ga2B"),
                                    amount = "-0.500913 BTC",
                                    timestamp = "8:41",
                                    status = TransactionState.Content.Status.Confirmed,
                                    direction = TransactionState.Content.Direction.OUTGOING,
                                ),
                            ),
                        ),
                    ),
                ),
            ),
            event = consumedEvent(),
            isBalanceHidden = false,
        )
    }
}
