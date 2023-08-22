package com.tangem.feature.tokendetails.presentation.router

import com.tangem.features.tokendetails.navigation.TokenDetailsRouter

internal interface InnerTokenDetailsRouter : TokenDetailsRouter {

    /** Pop back stack */
    fun popBackStack()

    /** Open website by [url] */
    fun openUrl(url: String)
}
