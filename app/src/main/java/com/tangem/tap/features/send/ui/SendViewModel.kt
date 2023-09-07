package com.tangem.tap.features.send.ui

import androidx.lifecycle.*
import com.tangem.domain.settings.IsBalanceHiddenUseCase
import com.tangem.tap.features.send.redux.AmountAction
import com.tangem.tap.features.shop.domain.GetShopifySalesProductsUseCase
import com.tangem.tap.features.shop.domain.ShopifyOrderingAvailabilityUseCase
import com.tangem.tap.features.shop.redux.ShopAction
import com.tangem.tap.proxy.AppStateHolder
import com.tangem.tap.store
import com.tangem.utils.coroutines.CoroutineDispatcherProvider
import com.tangem.utils.coroutines.runCatching
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Send screen view model
 *
 * @property dispatchers                        coroutine dispatchers provider
 * @property appStateHolder                     redux state holder
 *
 * @author Andrew Khokhlov on 15/06/2023
 */
@HiltViewModel
internal class SendViewModel @Inject constructor(
    private val dispatchers: CoroutineDispatcherProvider,
    private val appStateHolder: AppStateHolder,
    private val isBalanceHiddenUseCase: IsBalanceHiddenUseCase,
) : ViewModel(), DefaultLifecycleObserver {

    override fun onCreate(owner: LifecycleOwner) {
        isBalanceHiddenUseCase()
            .flowWithLifecycle(owner.lifecycle)
            .flowOn(dispatchers.io)
            .onEach { isBalanceHidden ->
                withContext(dispatchers.main) {
                    appStateHolder.mainStore?.dispatch(AmountAction.HideBalance(isBalanceHidden))
                }
            }
            .launchIn(viewModelScope)
    }
}
