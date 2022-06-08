package com.tangem.tap.features.home.compose.uiTools

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.tangem.tap.features.home.compose.StoriesScreen
import com.tangem.tap.features.home.compose.content.*

/**
 * Created by Anton Zhilenkov on 07/06/2022.
 */
@Preview
@Composable
fun StoriesScreenPreview() {
    StoriesScreen(onScanButtonClick = {}, onShopButtonClick = {}, onSearchTokensClick = {})
}

@Preview
@Composable
fun Stories1Preview() {
    FirstStoriesContent(false, 8000) {}
}

@Preview
@Composable
private fun RevolutionaryWalletPreview() {
    StoriesRevolutionaryWallet(6000)
}

@Preview
@Composable
private fun UltraSecureBackupPreview() {
    StoriesUltraSecureBackup(false, 6000)
}

@Preview
@Composable
private fun CurrenciesPreview() {
    StoriesCurrencies(false, 6000)
}

@Preview
@Composable
private fun Web3Preview() {
    StoriesWeb3(false, 6000)
}

@Preview
@Composable
private fun WalletForEveryonePreview() {
    StoriesWalletForEveryone(6000)
}