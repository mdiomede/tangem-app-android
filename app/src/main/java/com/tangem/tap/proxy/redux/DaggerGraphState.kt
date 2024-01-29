package com.tangem.tap.proxy.redux

import com.tangem.datasource.connection.NetworkConnectionManager
import com.tangem.domain.appcurrency.repository.AppCurrencyRepository
import com.tangem.domain.apptheme.repository.AppThemeModeRepository
import com.tangem.domain.balancehiding.repositories.BalanceHidingRepository
import com.tangem.domain.card.ScanCardProcessor
import com.tangem.domain.card.ScanCardUseCase
import com.tangem.domain.card.repository.CardSdkConfigRepository
import com.tangem.domain.card.repository.DerivationsRepository
import com.tangem.domain.tokens.repository.CurrenciesRepository
import com.tangem.domain.tokens.repository.NetworksRepository
import com.tangem.domain.walletmanager.WalletManagersFacade
import com.tangem.domain.wallets.repository.WalletsRepository
import com.tangem.feature.qrscanning.QrScanningRouter
import com.tangem.features.managetokens.featuretoggles.ManageTokensFeatureToggles
import com.tangem.features.managetokens.navigation.ManageTokensRouter
import com.tangem.features.send.api.featuretoggles.SendFeatureToggles
import com.tangem.features.send.api.navigation.SendRouter
import com.tangem.features.tester.api.TesterFeatureToggles
import com.tangem.features.tester.api.TesterRouter
import com.tangem.features.tokendetails.navigation.TokenDetailsRouter
import com.tangem.features.wallet.navigation.WalletRouter
import com.tangem.tap.domain.walletconnect2.domain.WalletConnectInteractor
import com.tangem.tap.domain.walletconnect2.domain.WalletConnectRepository
import com.tangem.tap.domain.walletconnect2.domain.WalletConnectSessionsRepository
import com.tangem.tap.features.customtoken.api.featuretoggles.CustomTokenFeatureToggles
import com.tangem.tap.proxy.AppStateHolder
import org.rekotlin.StateType

data class DaggerGraphState(
    val testerRouter: TesterRouter? = null,
    val networkConnectionManager: NetworkConnectionManager? = null,
    val customTokenFeatureToggles: CustomTokenFeatureToggles? = null,
    val scanCardUseCase: ScanCardUseCase? = null,
    val walletRouter: WalletRouter? = null,
    val walletConnectRepository: WalletConnectRepository? = null,
    val walletConnectSessionsRepository: WalletConnectSessionsRepository? = null,
    val walletConnectInteractor: WalletConnectInteractor? = null,
    val tokenDetailsRouter: TokenDetailsRouter? = null,
    val manageTokensFeatureToggles: ManageTokensFeatureToggles? = null,
    val manageTokensRouter: ManageTokensRouter? = null,
    val scanCardProcessor: ScanCardProcessor? = null,
    val cardSdkConfigRepository: CardSdkConfigRepository? = null,
    val appCurrencyRepository: AppCurrencyRepository? = null,
    val walletManagersFacade: WalletManagersFacade? = null,
    val appStateHolder: AppStateHolder? = null,
    val appThemeModeRepository: AppThemeModeRepository? = null,
    val balanceHidingRepository: BalanceHidingRepository? = null,
    val walletsRepository: WalletsRepository? = null,
    val networksRepository: NetworksRepository? = null,
    val sendFeatureToggles: SendFeatureToggles? = null,
    val sendRouter: SendRouter? = null,
    val qrScanningRouter: QrScanningRouter? = null,

    // FIXME: It is used only for TokensList screen. Remove after refactoring of TokensList
    val currenciesRepository: CurrenciesRepository? = null,
    val derivationsRepository: DerivationsRepository? = null,
    val testerFeatureToggles: TesterFeatureToggles? = null,
) : StateType {

    inline fun <reified T> get(getDependency: DaggerGraphState.() -> T?): T {
        return requireNotNull(getDependency()) {
            "${T::class.simpleName} isn't initialized "
        }
    }
}
