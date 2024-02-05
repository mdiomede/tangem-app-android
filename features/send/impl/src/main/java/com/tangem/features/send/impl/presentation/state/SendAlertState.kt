package com.tangem.features.send.impl.presentation.state

import androidx.compose.runtime.Immutable
import com.tangem.core.ui.extensions.TextReference
import com.tangem.core.ui.extensions.resourceReference
import com.tangem.core.ui.extensions.wrappedList
import com.tangem.features.send.impl.R

@Immutable
internal sealed class SendAlertState {

    abstract val title: TextReference?
    abstract val message: TextReference
    open val confirmButtonText: TextReference = resourceReference(id = R.string.common_ok)
    open val onConfirmClick: (() -> Unit)? = null

    data class GenericError(
        override val title: TextReference? = resourceReference(id = R.string.send_alert_transaction_failed_title),
        override val onConfirmClick: (() -> Unit),
    ) : SendAlertState() {
        override val message: TextReference = resourceReference(R.string.common_unknown_error)
        override val confirmButtonText: TextReference =
            resourceReference(id = R.string.send_alert_button_request_support)
    }

    data class TransactionError(
        val code: String,
        val cause: String?,
        val causeTextReference: TextReference? = null,
        override val onConfirmClick: (() -> Unit),
    ) : SendAlertState() {
        override val title: TextReference = resourceReference(id = R.string.send_alert_transaction_failed_title)
        override val message: TextReference = resourceReference(
            id = R.string.send_alert_transaction_failed_text,
            formatArgs = wrappedList(causeTextReference ?: cause.orEmpty(), code),
        )
        override val confirmButtonText: TextReference =
            resourceReference(id = R.string.send_alert_button_request_support)
    }

    data class DemoMode(
        override val onConfirmClick: () -> Unit,
    ) : SendAlertState() {
        override val title: TextReference = resourceReference(id = R.string.warning_demo_mode_title)
        override val message: TextReference = resourceReference(id = R.string.warning_demo_mode_message)
    }

    object FeeIncreased : SendAlertState() {
        override val title: TextReference? = null
        override val message: TextReference = resourceReference(id = R.string.send_notification_high_fee_title)
    }
}