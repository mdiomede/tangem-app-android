package com.tangem.core.ui.components.buttons.actions

import androidx.annotation.DrawableRes

/**
 * Action button config
 *
 * @property text      text
 * @property iconResId icon resource id
 * @property onClick   lambda be invoked when action component is clicked
 * @property enabled   enabled
 * @property dimContent determines whether the button content will be dimmed
 *
 * @author Andrew Khokhlov on 23/06/2023
 */
data class ActionButtonConfig(
    val text: String,
    @DrawableRes val iconResId: Int,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
    val dimContent: Boolean = false,
)
