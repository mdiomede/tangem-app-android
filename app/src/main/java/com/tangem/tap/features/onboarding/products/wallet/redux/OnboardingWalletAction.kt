package com.tangem.tap.features.onboarding.products.wallet.redux

import android.graphics.Bitmap
import org.rekotlin.Action

sealed class OnboardingWalletAction : Action {
    object Init : OnboardingWalletAction()
    object GetToCreateWalletStep : OnboardingWalletAction()
    object GetToSaltPayStep : OnboardingWalletAction()
    object CreateWallet : OnboardingWalletAction()
    object Done : OnboardingWalletAction()
    object FinishOnboarding : OnboardingWalletAction()

    object ResumeBackup : OnboardingWalletAction()

    object LoadArtwork : OnboardingWalletAction()
    class SetArtworkUrl(val artworkUrl: String?) : OnboardingWalletAction()

    object OnBackPressed : OnboardingWalletAction()
}

sealed class BackupAction : Action {

    object DetermineBackupStep : BackupAction()
    object IntroduceBackup : BackupAction()
    object StartBackup : BackupAction()
    object DismissBackup : BackupAction()

    object StartAddingPrimaryCard : BackupAction()
    object ScanPrimaryCard : BackupAction()

    /**
     * Check for unfinished backup of standard Wallet cards.
     * For SaltPay cards unfinished backup resumed after scanning the card on HomeScreen through Onboarding.Start.
     * See more Onboarding.Start, CheckForUnfinishedBackup, StartForUnfinishedBackup
     */
    object CheckForUnfinishedBackup : BackupAction()

    object StartAddingBackupCards : BackupAction()
    object AddBackupCard : BackupAction() {
        object Success : BackupAction()
    }

    data class LoadBackupCardArtwork(
        val cardId: CardId,
        val cardPublicKey: ByteArray,
    ) : BackupAction() {
        data class Success(val cardId: CardId, val artwork: Bitmap)
    }

    object FinishAddingBackupCards : BackupAction()

    object ShowAccessCodeInfoScreen : BackupAction()
    object ShowEnterAccessCodeScreen : BackupAction()
    data class CheckAccessCode(val accessCode: String) : BackupAction()
    data class SetAccessCodeError(val error: AccessCodeError?) : BackupAction()
    data class SaveFirstAccessCode(val accessCode: String) : BackupAction()
    object ShowReenterAccessCodeScreen : BackupAction()
    data class SaveAccessCodeConfirmation(val accessCodeConfirmation: String) : BackupAction()
    object OnAccessCodeDialogClosed : BackupAction()

    object PrepareToWritePrimaryCard : BackupAction()
    object WritePrimaryCard : BackupAction()
    data class PrepareToWriteBackupCard(val cardNumber: Int) : BackupAction()
    data class WriteBackupCard(val cardNumber: Int) : BackupAction()

    /**
     * It always calls for the SaltPay cards when resuming the activation process.
     */
    data class FinishBackup(val withAnalytics: Boolean = true) : BackupAction()

    object DiscardBackup : BackupAction()
    object DiscardSavedBackup : BackupAction()
    object ResumeFoundUnfinishedBackup : BackupAction()
}
