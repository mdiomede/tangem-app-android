package com.tangem.features.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.tangem.core.ui.UiDependencies
import com.tangem.core.ui.screen.ComposeFragment
import com.tangem.features.details.component.DetailsComponent
import com.tangem.features.details.ui.DetailsScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
internal class DetailsFragment : ComposeFragment() {

    @Inject
    override lateinit var uiDependencies: UiDependencies

    private val component: MutableState<DetailsComponent?> = mutableStateOf(value = null)

    @Composable
    override fun ScreenContent(modifier: Modifier) {
        val component = component.value ?: return

        DetailsScreen(
            modifier = modifier,
            component = component,
        )
    }

    companion object {

        fun newInstance(component: DetailsComponent): DetailsFragment {
            return DetailsFragment().apply {
                this.component.value = component
            }
        }
    }
}
