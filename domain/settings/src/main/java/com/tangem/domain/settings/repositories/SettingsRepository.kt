package com.tangem.domain.settings.repositories

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    suspend fun isUserAlreadyRateApp(): Boolean

    suspend fun shouldShowSaveUserWalletScreen(): Boolean

    fun isBalanceHiddenEvents(): Flow<Boolean>

    suspend fun storeBalanceHiddenFlag(isBalanceHidden: Boolean)

    suspend fun isBalanceHidden(): Boolean
}
