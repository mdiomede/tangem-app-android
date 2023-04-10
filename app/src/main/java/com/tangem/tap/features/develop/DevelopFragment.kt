package com.tangem.tap.features.develop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tangem.core.ui.res.TangemTheme
import com.tangem.feature.onboarding.presentation.wallet2.model.OnboardingSeedPhraseStep
import com.tangem.feature.onboarding.presentation.wallet2.ui.AboutSeedPhraseScreen
import com.tangem.feature.onboarding.presentation.wallet2.ui.CheckSeedPhraseScreen
import com.tangem.feature.onboarding.presentation.wallet2.ui.ImportSeedPhraseScreen
import com.tangem.feature.onboarding.presentation.wallet2.ui.IntroScreen
import com.tangem.feature.onboarding.presentation.wallet2.ui.YourSeedPhraseScreen
import com.tangem.feature.onboarding.presentation.wallet2.viewmodel.SeedPhraseViewModel
import com.tangem.tap.features.details.ui.common.EmptyTopBarWithNavigation
import com.tangem.wallet.R
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Anton Zhilenkov on 11.03.2023.
 * Temporary fragment to test Wallet2 onboarding screens
 */
@AndroidEntryPoint
class DevelopFragment : Fragment() {

    private val viewModel by viewModels<SeedPhraseViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(inflater.context).apply {
            setContent {
                ScreenContent()
            }
        }
    }

    @Composable
    private fun ScreenContent() {
        TangemTheme {
            Scaffold(
                topBar = {
                    EmptyTopBarWithNavigation(
                        onBackClick = { },
                    )
                    TopAppBar(
                        title = { ToolbarTitleView(viewModel.currentStep) },
                        navigationIcon = {
                            IconButton(onClick = { }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_back_24),
                                    contentDescription = null,
                                    tint = TangemTheme.colors.icon.primary1,
                                )
                            }
                        },
                        backgroundColor = TangemTheme.colors.background.primary,
                        elevation = 0.dp,
                    )
                },
                modifier = Modifier
                    .systemBarsPadding()
                    .background(color = TangemTheme.colors.background.secondary),
                content = { padding ->
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 8.dp),
                        ) {
                            val maxProgress = 1f
                            val maxStateProgress = OnboardingSeedPhraseStep.values().size * 2
                            val viewProgress = viewModel.currentStep.ordinal + 2f
                            val currentProgress = maxProgress * viewProgress / maxStateProgress
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                color = Color.Black,
                                backgroundColor = Color.Black.copy(alpha = 0.081f),
                                progress = currentProgress,
                            )
                        }

                        val modifier = Modifier.padding(padding)
                        when (viewModel.currentStep) {
                            OnboardingSeedPhraseStep.Intro -> {
                                IntroScreen(
                                    modifier = modifier,
                                    state = viewModel.uiState.introState,
                                )
                            }
                            OnboardingSeedPhraseStep.AboutSeedPhrase -> {
                                AboutSeedPhraseScreen(
                                    modifier = modifier,
                                    state = viewModel.uiState.aboutState,
                                )
                            }
                            OnboardingSeedPhraseStep.YourSeedPhrase -> {
                                YourSeedPhraseScreen(
                                    modifier = modifier,
                                    state = viewModel.uiState.yourSeedPhraseState,
                                )
                            }
                            OnboardingSeedPhraseStep.CheckSeedPhrase -> {
                                CheckSeedPhraseScreen(
                                    modifier = modifier,
                                    state = viewModel.uiState.checkSeedPhraseState,
                                )
                            }
                            OnboardingSeedPhraseStep.ImportSeedPhrase -> {
                                ImportSeedPhraseScreen(
                                    modifier = modifier,
                                    state = viewModel.uiState.importSeedPhraseState,
                                )
                            }
                        }
                    }
                },
            )
        }
    }

    @Composable
    private fun ToolbarTitleView(currentStep: OnboardingSeedPhraseStep) {
        val text = when (currentStep) {
            OnboardingSeedPhraseStep.Intro -> "Tangem"
            OnboardingSeedPhraseStep.AboutSeedPhrase -> "Создать кошелек"
            OnboardingSeedPhraseStep.YourSeedPhrase -> "Создать кошелек"
            OnboardingSeedPhraseStep.CheckSeedPhrase -> "Создать кошелек"
            OnboardingSeedPhraseStep.ImportSeedPhrase -> "Импортировать кошелек"
        }
        Text(text)
    }
}
