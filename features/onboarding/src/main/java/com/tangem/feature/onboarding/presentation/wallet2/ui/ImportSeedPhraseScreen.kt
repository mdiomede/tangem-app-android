package com.tangem.feature.onboarding.presentation.wallet2.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import com.tangem.core.ui.components.PrimaryButton
import com.tangem.core.ui.components.PrimaryButtonIconLeft
import com.tangem.core.ui.components.TangemTextFieldsDefault
import com.tangem.core.ui.res.TangemTheme
import com.tangem.feature.onboarding.R
import com.tangem.feature.onboarding.presentation.wallet2.model.ImportSeedPhraseState
import com.tangem.feature.onboarding.presentation.wallet2.ui.components.DescriptionSubTitleText
import com.tangem.feature.onboarding.presentation.wallet2.ui.components.OnboardingActionBlock
import com.tangem.feature.onboarding.presentation.wallet2.ui.components.OnboardingDescriptionBlock

/**
 * Created by Anton Zhilenkov on 11.03.2023.
 */
@Composable
fun ImportSeedPhraseScreen(
    state: ImportSeedPhraseState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier.weight(1f),
        ) {
            Column {
                OnboardingDescriptionBlock(modifier) {
                    DescriptionSubTitleText(text = stringResource(id = R.string.onboarding_seed_import_message))
                }
                PhraseBlock(
                    modifier = Modifier
                        .padding(top = TangemTheme.dimens.size62)
                        .padding(horizontal = TangemTheme.dimens.size16),
                    state = state,
                )
                SuggestionsBlock(
                    state = state,
                )
            }
        }
        Box(
            modifier = Modifier.wrapContentSize(),
        ) {
            OnboardingActionBlock(
                firstActionContent = {
                    PrimaryButtonIconLeft(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = TangemTheme.dimens.size16),
                        text = stringResource(id = R.string.onboarding_create_wallet_button_create_wallet),
                        icon = painterResource(id = R.drawable.ic_tangem_24),
                        enabled = state.buttonCreateWallet.enabled,
                        showProgress = state.buttonCreateWallet.showProgress,
                        onClick = state.buttonCreateWallet.onClick,
                    )
                },
            )
        }
    }
}

@Composable
private fun PhraseBlock(
    state: ImportSeedPhraseState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(TangemTheme.dimens.size142),
            value = state.tvSeedPhrase.textFieldValue,
            onValueChange = state.tvSeedPhrase.onTextFieldValueChanged,
            textStyle = TangemTheme.typography.body1,
            singleLine = false,
            colors = TangemTextFieldsDefault.defaultTextFieldColors,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(TangemTheme.dimens.size32),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxSize(),
                text = "TODO: implement error message",
                style = TangemTheme.typography.caption.copy(
                    color = TangemTheme.colors.text.warning,
                ),
            )
        }
    }
}

@Composable
private fun SuggestionsBlock(
    state: ImportSeedPhraseState,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        enter = fadeIn() + slideIn(initialOffset = { IntOffset(200, 0) }),
        exit = slideOut(targetOffset = { IntOffset(-200, 0) }) + fadeOut(),
        visible = state.suggestionsList.isNotEmpty(),
    ) {
        LazyRow(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = TangemTheme.dimens.size16),
        ) {
            items(state.suggestionsList.size) { index ->
                PrimaryButton(
                    modifier = Modifier
                        .height(TangemTheme.dimens.size46)
                        .padding(all = TangemTheme.dimens.size4),
                    text = state.suggestionsList[index],
                    onClick = { state.onSuggestedPhraseClick(index) },
                )
            }
        }
    }
}
