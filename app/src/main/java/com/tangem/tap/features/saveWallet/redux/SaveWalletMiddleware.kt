package com.tangem.tap.features.saveWallet.redux

import com.tangem.common.*
import com.tangem.common.core.TangemSdkError
import com.tangem.common.extensions.guard
import com.tangem.common.routing.AppRoute
import com.tangem.common.routing.utils.popTo
import com.tangem.core.analytics.Analytics
import com.tangem.domain.wallets.builder.UserWalletBuilder
import com.tangem.domain.wallets.models.UserWallet
import com.tangem.tap.*
import com.tangem.tap.common.analytics.events.AnalyticsParam
import com.tangem.tap.common.analytics.events.MainScreen
import com.tangem.tap.common.analytics.events.Onboarding
import com.tangem.tap.common.extensions.*
import com.tangem.tap.common.redux.AppState
import com.tangem.tap.proxy.redux.DaggerGraphState
import com.tangem.utils.coroutines.JobHolder
import com.tangem.utils.coroutines.saveIn
import kotlinx.coroutines.launch
import org.rekotlin.Middleware
import timber.log.Timber

internal class SaveWalletMiddleware {

    private val saveWalletJobHolder = JobHolder()

    val middleware: Middleware<AppState> = { _, stateProvider ->
        { next ->
            { action ->
                val state = stateProvider()
                if (action is SaveWalletAction && state != null) {
                    handleAction(action, state.saveWalletState)
                }
                next(action)
            }
        }
    }

    private fun handleAction(action: SaveWalletAction, state: SaveWalletState) {
        when (action) {
            is SaveWalletAction.AllowToUseBiometrics -> allowToUseBiometrics(state)
            is SaveWalletAction.EnrollBiometrics.Enroll -> enrollBiometrics()
            is SaveWalletAction.Dismiss -> dismiss(state)
            is SaveWalletAction.SaveWalletAfterBackup -> saveWalletAfterBackup(state, action.hasBackupError)
            is SaveWalletAction.AllowToUseBiometrics.Success,
            is SaveWalletAction.AllowToUseBiometrics.Error,
            is SaveWalletAction.ProvideBackupInfo,
            is SaveWalletAction.CloseError,
            is SaveWalletAction.EnrollBiometrics,
            is SaveWalletAction.EnrollBiometrics.Cancel,
            -> Unit
        }
    }

    private fun saveWalletAfterBackup(state: SaveWalletState, hasBackupError: Boolean) {
        scope.launch {
            val backupInfo = state.backupInfo ?: error("Backup info is null")

            val walletNameGenerateUseCase = store.inject(DaggerGraphState::generateWalletNameUseCase)
            val userWallet = UserWalletBuilder(backupInfo.scanResponse, walletNameGenerateUseCase)
                .backupCardsIds(state.backupInfo.backupCardsIds)
                .hasBackupError(hasBackupError)
                .build()
                .guard {
                    Timber.e("User wallet not created")
                    return@launch
                }

            val userWalletsListManager = store.inject(DaggerGraphState::generalUserWalletsListManager)
            userWalletsListManager.save(userWallet, canOverride = true)
                .flatMap {
                    saveAccessCodeIfNeeded(accessCode = backupInfo.accessCode, cardsInWallet = userWallet.cardsInWallet)
                }
                .doOnFailure { error ->
                    Timber.e(error, "Unable to save user wallet")
                }
                .doOnSuccess { mainScope.launch { store.onUserWalletSelected(userWallet) } }
                .doOnResult { navigateToWallet() }
        }
    }

    private fun enrollBiometrics() {
        activityResultCaller.openSystemBiometrySettings()
    }

    private fun allowToUseBiometrics(state: SaveWalletState) = scope.launch {
        if (tangemSdkManager.checkNeedEnrollBiometrics()) {
            store.dispatchWithMain(SaveWalletAction.EnrollBiometrics)
            return@launch
        }

        if (state.backupInfo != null) {
            // TODO: Remove after onboarding refactoring
            Analytics.send(Onboarding.EnableBiometrics(AnalyticsParam.OnOffState.On))
        } else {
            Analytics.send(MainScreen.EnableBiometrics(AnalyticsParam.OnOffState.On))
        }

        /*
         * We don't need to save user wallet if it is not created from backup info,
         * because it will be automatically saved on UserWalletsListManager switch
         */
        val userWalletsListManager = store.inject(DaggerGraphState::generalUserWalletsListManager)
        val selectedUserWallet = userWalletsListManager.selectedUserWalletSync.guard {
            val error = IllegalStateException("No selected user wallet")
            Timber.e(error, "Unable to save user wallet")
            store.dispatchWithMain(
                SaveWalletAction.AllowToUseBiometrics.Error(TangemSdkError.ExceptionError(error)),
            )
            return@launch
        }

        handleSuccessAllowing(selectedUserWallet)
    }.saveIn(saveWalletJobHolder)

    private suspend fun handleSuccessAllowing(userWallet: UserWallet) {
        store.inject(DaggerGraphState::walletsRepository).saveShouldSaveUserWallets(item = true)

        store.inject(DaggerGraphState::settingsRepository).setShouldSaveAccessCodes(value = true)

        store.inject(DaggerGraphState::cardSdkConfigRepository).setAccessCodeRequestPolicy(
            isBiometricsRequestPolicy = userWallet.hasAccessCode,
        )

        store.dispatchWithMain(SaveWalletAction.AllowToUseBiometrics.Success)
        store.dispatchNavigationAction { popTo<AppRoute.Wallet>() }
    }

    private fun dismiss(state: SaveWalletState) {
        if (state.backupInfo != null) {
            // TODO: Remove after onboarding refactoring
            Analytics.send(Onboarding.EnableBiometrics(AnalyticsParam.OnOffState.Off))
        } else {
            Analytics.send(MainScreen.EnableBiometrics(AnalyticsParam.OnOffState.Off))
        }
    }

    private suspend fun saveAccessCodeIfNeeded(
        accessCode: String?,
        cardsInWallet: Set<String>,
    ): CompletionResult<Unit> {
        return when {
            accessCode.isNullOrBlank() -> {
                CompletionResult.Success(Unit)
            }
            else -> {
                tangemSdkManager.saveAccessCode(
                    accessCode = accessCode,
                    cardsIds = cardsInWallet,
                )
            }
        }
    }

    private fun navigateToWallet() {
        store.dispatchNavigationAction {
            replaceAll(AppRoute.Wallet)
        }
    }
}
