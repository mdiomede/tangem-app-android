package com.tangem.domain.settings

import com.tangem.domain.settings.repositories.SettingsRepository

/**
 * @author Andrew Khokhlov on 24/11/2023
 */
class SetWalletsScrollPreviewIsShown(
    private val settingsRepository: SettingsRepository,
) {

    suspend operator fun invoke() = settingsRepository.setWalletScrollPreviewAvailability(isEnabled = false)
}
