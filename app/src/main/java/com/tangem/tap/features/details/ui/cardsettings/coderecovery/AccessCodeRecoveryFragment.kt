package com.tangem.tap.features.details.ui.cardsettings.coderecovery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import com.tangem.core.ui.res.TangemTheme
import com.tangem.tap.common.redux.navigation.NavigationAction
import com.tangem.tap.features.details.redux.DetailsState
import com.tangem.tap.store
import com.tangem.wallet.R
import org.rekotlin.StoreSubscriber

class AccessCodeRecoveryFragment : Fragment(), StoreSubscriber<DetailsState> {

    private val viewModel = AccessCodeRecoveryViewModel(store)

    private var screenState: MutableState<AccessCodeRecoveryScreenState> =
        mutableStateOf(viewModel.updateState(store.state.detailsState.cardSettingsState?.accessCodeRecovery))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.fade)
        exitTransition = inflater.inflateTransition(R.transition.fade)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                isTransitionGroup = true
                TangemTheme {
                    AccessCodeRecoveryScreen(
                        state = screenState.value,
                        onBackClick = { store.dispatch(NavigationAction.PopBackTo()) },
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        store.subscribe(this) { state ->
            state.skipRepeats { oldState, newState ->
                oldState.detailsState == newState.detailsState
            }.select { it.detailsState }
        }
    }

    override fun onStop() {
        super.onStop()
        store.unsubscribe(this)
    }

    override fun newState(state: DetailsState) {
        if (activity == null || view == null) return
        screenState.value =
            viewModel.updateState(store.state.detailsState.cardSettingsState?.accessCodeRecovery)
    }
}