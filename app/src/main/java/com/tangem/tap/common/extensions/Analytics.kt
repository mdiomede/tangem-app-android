package com.tangem.tap.common.extensions

import com.tangem.core.analytics.Analytics
import com.tangem.domain.common.ScanResponse
import com.tangem.tap.common.analytics.paramsInterceptor.CardContextInterceptor

/**
 * Created by Anton Zhilenkov on 17.02.2023.
 */
fun Analytics.addCardContext(scanResponse: ScanResponse) {
    addParamsInterceptor(CardContextInterceptor(scanResponse))
}

fun Analytics.eraseCardContext() {
    removeParamsInterceptor(CardContextInterceptor.id())
}
