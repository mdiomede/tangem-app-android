package com.tangem.feature.learn2earn.domain.api

/**
 * Interface, a wrapper that allows us to get values from the AppStateHolder located in the app module
 *
 * @author Anton Zhilenkov on 28.06.2023.
 */
interface Learn2earnDependencyProvider {

    fun getUserCountryCodeProvider(): () -> String

    fun getWebViewAuthCredentialsProvider(): () -> String?
}
