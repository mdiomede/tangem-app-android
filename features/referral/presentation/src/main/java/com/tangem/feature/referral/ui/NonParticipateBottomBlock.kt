package com.tangem.feature.referral.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.tangem.core.ui.components.PrimaryButtonIconRight
import com.tangem.core.ui.res.TangemTheme
import com.tangem.feature.referral.presentation.R

@Composable
internal fun NonParticipateBottomBlock(onAgreementClick: () -> Unit, onParticipateClick: () -> Unit) {
    Column {
        AgreementText(
            firstPartResId = R.string.referral_tos_not_enroled_prefix,
            onClick = onAgreementClick,
        )
        PrimaryButtonIconRight(
            text = stringResource(id = R.string.referral_button_participate),
            icon = painterResource(id = R.drawable.ic_tangem_24),
            onClick = onParticipateClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = TangemTheme.dimens.spacing16),
        )
    }
}

@Preview(widthDp = 360, showBackground = true)
@Composable
private fun Preview_NonParticipateBottomBlock_InLightTheme() {
    TangemTheme(isDark = false) {
        Column(Modifier.background(TangemTheme.colors.background.primary)) {
            NonParticipateBottomBlock(onAgreementClick = {}, onParticipateClick = {})
        }
    }
}

@Preview(widthDp = 360, showBackground = true)
@Composable
private fun Preview_NonParticipateBottomBlock_InDarkTheme() {
    TangemTheme(isDark = true) {
        Column(Modifier.background(TangemTheme.colors.background.primary)) {
            NonParticipateBottomBlock(onAgreementClick = {}, onParticipateClick = {})
        }
    }
}
