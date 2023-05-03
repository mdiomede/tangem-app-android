package com.tangem.feature.onboarding.api

import androidx.compose.runtime.Composable
import com.tangem.feature.onboarding.presentation.wallet2.viewmodel.SeedPhraseViewModel

/**
 * @author Anton Zhilenkov on 25.04.2023.
 */
interface OnboardingSeedPhraseApi {
    @Suppress("TopLevelComposableFunctions")
    @Composable
    fun ScreenContent(viewModel: SeedPhraseViewModel, maxProgress: Int)
}
