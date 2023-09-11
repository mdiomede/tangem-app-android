package com.tangem.data.settings

import com.tangem.data.source.preferences.PreferencesDataSource
import com.tangem.datasource.local.appcurrency.HiddenBalanceStore
import com.tangem.domain.settings.repositories.SettingsRepository
import com.tangem.utils.coroutines.CoroutineDispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

internal class DefaultSettingsRepository(
    private val preferencesDataSource: PreferencesDataSource,
    private val dispatchers: CoroutineDispatcherProvider,
    private val isBalanceHiddenStore: HiddenBalanceStore,
) : SettingsRepository {

    override suspend fun isUserAlreadyRateApp(): Boolean {
        return withContext(dispatchers.io) {
            preferencesDataSource.appRatingLaunchObserver.isReadyToShow()
        }
    }

    override suspend fun shouldShowSaveUserWalletScreen(): Boolean {
        return withContext(dispatchers.io) { preferencesDataSource.shouldShowSaveUserWalletScreen }
    }

    override fun isBalanceHiddenEvents(): Flow<Boolean> {
        return isBalanceHiddenStore.get()
    }

    override suspend fun storeBalanceHiddenFlag(isBalanceHidden: Boolean) {
        isBalanceHiddenStore.store(isBalanceHidden)
    }

    override suspend fun isBalanceHidden(): Boolean {
        return isBalanceHiddenStore.getSyncOrFalse()
    }
}
