package com.tangem.tap.features.onboarding.products.wallet.saltPay

import com.tangem.tap.common.zendesk.ZendeskConfig

/**
 * Created by Anton Zhilenkov on 13.10.2022.
 */
data class SaltPayConfig(
    val zendesk: ZendeskConfig? = null,
    val kycProvider: KYCProvider,
    val credentials: Credentials,
    val blockscoutCredentials: Credentials,
) {
    companion object {
        fun stub(): SaltPayConfig {
            return SaltPayConfig(
                kycProvider = KYCProvider("", "", "", ""),
                credentials = Credentials("", ""),
                blockscoutCredentials = Credentials("", ""),
            )
        }
    }
}

data class KYCProvider(
    val baseUrl: String,
    val externalIdParameterKey: String,
    val sidParameterKey: String,
    val sidValue: String,
)

data class Credentials(
    val user: String,
    val password: String,
) {
    val basicAuthToken: String by lazy { okhttp3.Credentials.basic(user, password) }
}







