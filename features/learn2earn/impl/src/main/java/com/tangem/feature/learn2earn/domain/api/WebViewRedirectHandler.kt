package com.tangem.feature.learn2earn.domain.api

import android.net.Uri

/**
 * Handler that helps determine what actions to take inside the WebView of the Learn2earnWebViewActivity after
 * a redirect has been processedWebViewRedirectHandler
 *
 * @author Anton Zhilenkov on 12.06.2023.
 */
interface WebViewRedirectHandler {
    fun handleRedirect(uri: Uri): RedirectConsequences
}

enum class RedirectConsequences {
    NOTHING,
    PROCEED,
    FINISH_SESSION,
}
