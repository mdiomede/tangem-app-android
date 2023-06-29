package com.tangem.feature.learn2earn.domain.api

/**
 * Handler that helps determine a result of the Learn2earnWebViewActivity webView actions.
 *
 * @author Anton Zhilenkov on 23.06.2023.
 */
interface WebViewResultHandler {
    fun handleResult(result: WebViewResult)
}

sealed class WebViewResult {
    object PromoCodeReceived : WebViewResult()
    object ReadyForAward : WebViewResult()
}
