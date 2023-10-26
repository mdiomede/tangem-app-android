package com.tangem.features.send.impl.presentation.send.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tangem.core.ui.components.bottomsheets.TangemBottomSheetDraggableHeader
import com.tangem.core.ui.res.TangemTheme
import com.tangem.features.send.impl.presentation.send.state.SendUiState

@Composable
fun SendScreen() {
    Column(
        modifier = Modifier
            .imePadding()
            .background(
                color = TangemTheme.colors.background.tertiary,
                shape = RoundedCornerShape(
                    topStart = TangemTheme.dimens.radius24,
                    topEnd = TangemTheme.dimens.radius24,
                ),
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TangemBottomSheetDraggableHeader(
            color = TangemTheme.colors.background.tertiary,
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .scrollable(state = rememberScrollState(), orientation = Orientation.Vertical),
        ) {
            SendScreenContent()
        }
        SendNavigationButtons(uiState = SendUiState.Content.Initial())
    }
}

@Composable
fun SendScreenContent() {
    // todo work in progress
}
