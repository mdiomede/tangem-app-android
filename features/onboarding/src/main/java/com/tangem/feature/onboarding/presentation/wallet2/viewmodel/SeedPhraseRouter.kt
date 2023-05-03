package com.tangem.feature.onboarding.presentation.wallet2.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * @author Anton Zhilenkov on 23.04.2023.
 */
class SeedPhraseRouter(
    private val onBack: () -> Unit,
    private val onOpenChat: () -> Unit,
    private val onOpenUriClick: (Uri) -> Unit,
) {

    var currentScreen by mutableStateOf(SeedPhraseScreen.Intro)
        private set

    fun navigateBack() {
        currentScreen = when (currentScreen) {
            SeedPhraseScreen.Intro -> {
                onBack.invoke()
                return
            }
            SeedPhraseScreen.AboutSeedPhrase -> SeedPhraseScreen.Intro
            SeedPhraseScreen.YourSeedPhrase -> SeedPhraseScreen.AboutSeedPhrase
            SeedPhraseScreen.CheckSeedPhrase -> SeedPhraseScreen.YourSeedPhrase
            SeedPhraseScreen.ImportSeedPhrase -> SeedPhraseScreen.AboutSeedPhrase
        }
    }

    fun openScreen(screen: SeedPhraseScreen) {
        currentScreen = screen
    }

    fun openChat() {
        onOpenChat.invoke()
    }

    fun openUri(uri: Uri) {
        onOpenUriClick.invoke(uri)
    }
}

enum class SeedPhraseScreen {
    Intro, AboutSeedPhrase, YourSeedPhrase, CheckSeedPhrase, ImportSeedPhrase
}
