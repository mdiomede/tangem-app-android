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
    private val isBalanceHiddenUseCase: IsBalanceHiddenUseCase
) : ViewModel(), DefaultLifecycleObserver {

    private val isBalanceHiddenFlow: StateFlow<Boolean> = isBalanceHiddenUseCase.invoke().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = true,
    )

    override fun onCreate(owner: LifecycleOwner) {
        isBalanceHiddenUseCase()
            .flowWithLifecycle(owner.lifecycle)
            .onEach { isBalanceHidden ->
                appStateHolder.mainStore?.dispatch(AmountAction.HideBalance(isBalanceHidden))
            }
            // .flowOn(dispatchers.io)
            .launchIn(viewModelScope)
    }

}
