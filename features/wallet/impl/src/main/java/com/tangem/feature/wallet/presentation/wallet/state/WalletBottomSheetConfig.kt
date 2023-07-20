package com.tangem.feature.wallet.presentation.wallet.state

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.tangem.core.ui.extensions.TextReference
import com.tangem.core.ui.extensions.WrappedList
import com.tangem.core.ui.res.TangemColorPalette
import com.tangem.feature.wallet.impl.R

/**
 * Wallet bottom sheet config
 *
 * @property isShow           flag that determine if bottom sheet is shown
 * @property onDismissRequest lambda be invoked when bottom sheet is dismissed
 * @property content          content config
 *
 * @author Andrew Khokhlov on 14/07/2023
 */
// TODO: Finalize notification strings https://tangem.atlassian.net/browse/AND-4040
internal data class WalletBottomSheetConfig(
    val isShow: Boolean,
    val onDismissRequest: () -> Unit,
    val content: BottomSheetContentConfig,
) {

    sealed class BottomSheetContentConfig(
        open val title: TextReference,
        open val subtitle: TextReference,
        @DrawableRes open val iconResId: Int,
        open val tint: Color? = null,
        val primaryButtonConfig: ButtonConfig,
        val secondaryButtonConfig: ButtonConfig? = null,
    ) {

        data class ButtonConfig(val text: String, val onClick: () -> Unit, @DrawableRes val iconResId: Int? = null)

        data class UnlockWallets(
            val onUnlockClick: () -> Unit,
            val onScanClick: () -> Unit,
        ) : BottomSheetContentConfig(
            title = TextReference.Str(value = "Unlock needed"),
            subtitle = TextReference.Str(
                value = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor " +
                    "incididunt ut labore et dolore magna aliqua.",
            ),
            iconResId = R.drawable.ic_locked_24,
            tint = TangemColorPalette.Black,
            primaryButtonConfig = ButtonConfig(text = "Unlock", onClick = onUnlockClick),
            secondaryButtonConfig = ButtonConfig(
                text = "Scan card",
                onClick = onScanClick,
                iconResId = R.drawable.ic_tangem_24,
            ),
        )

        data class LikeTangemApp(
            val onRateTheAppClick: () -> Unit,
            val onShareClick: () -> Unit,
        ) : BottomSheetContentConfig(
            title = TextReference.Str(value = "Like Tangem App?"),
            subtitle = TextReference.Str(value = "How was your experience with our app? Let us know:"),
            iconResId = R.drawable.ic_star_24,
            tint = TangemColorPalette.Tangerine,
            primaryButtonConfig = ButtonConfig(text = "Rate the app", onClick = onRateTheAppClick),
            secondaryButtonConfig = ButtonConfig(text = "Share feedback", onClick = onShareClick),
        )

        data class MultiWalletAlreadySignedHashes(val onLearnClick: () -> Unit) : BottomSheetContentConfig(
            title = TextReference.Res(
                id = R.string.warning_important_security_info,
                formatArgs = WrappedList(listOf("\u26A0")),
            ),
            subtitle = TextReference.Res(id = R.string.warning_signed_tx_previously),
            iconResId = R.drawable.img_attention_20,
            tint = null,
            primaryButtonConfig = ButtonConfig(text = "Learn more", onClick = onLearnClick),
        )
    }
}